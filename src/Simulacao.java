import java.util.ArrayList;
import java.util.List;

/**
 * O "relógio" do sistema. Cada rodada (método executarRodada()):
 *   1. postos terminam atendimentos
 *   2. chegam clientes novos (aleatório)
 *   3. alguém pode desistir (aleatório)
 *   4. postos opcionais podem ligar/desligar (aleatório)
 *   5. postos livres chamam a próxima senha (regra 2N:1P)
 *   6. mostra o painel
 * Encerra quando para de chegar gente e a fila e os postos esvaziam (terminou()).
 *
 * A classe foi pensada para também ser usada por um front web: por isso
 * executarRodada() e terminou() são públicos, e as mensagens de cada rodada
 * ficam guardadas em uma lista (além de serem impressas no console).
 */
public class Simulacao {
    // ---- parâmetros da simulação (mexa à vontade) ----
    private static final int RODADAS_COM_CHEGADA = 20;   // depois disso ninguém mais chega
    private static final int MAX_CHEGADAS_POR_RODADA = 3;
    private static final double CHANCE_PRIORITARIA = 0.3;
    private static final double CHANCE_DESISTENCIA = 0.15;
    private static final double CHANCE_ALTERNAR_POSTO = 0.1;
    private static final int DURACAO_MIN = 2;
    private static final int DURACAO_MAX = 6;

    private final FilaPrioritaria fila = new FilaPrioritaria();
    private final GerenciadorPostos postos = new GerenciadorPostos();
    private final Pilha<Senha> atendidas = new Pilha<>();

    private int proximoNormal = 1;
    private int proximoPrioritario = 1;
    private int desistencias = 0;
    private int rodada = 0;
    private final long pausaMs;

    // Mensagens (chegadas, desistências, chamadas) da rodada que acabou de rodar.
    // Fica guardada aqui para um front web poder ler depois de chamar executarRodada().
    private List<String> mensagensRodada = new ArrayList<>();

    public Simulacao(long pausaMs) {
        this.pausaMs = pausaMs;
    }

    public void executar() {
        System.out.println("=== SISTEMA DE SENHAS: INÍCIO DO ATENDIMENTO ===");

        while (!terminou()) {
            executarRodada();
            pausar();
        }

        encerrar();
    }

    /**
     * Executa uma única rodada da simulação (um "tick" do relógio).
     * Pode ser chamado direto por um front web, uma rodada por vez.
     */
    public void executarRodada() {
        rodada++;
        mensagensRodada = new ArrayList<>(); // começa a lista da rodada atual zerada
        System.out.println("\n----------------- RODADA " + rodada + " -----------------");

        postos.passarRodada();
        if (rodada <= RODADAS_COM_CHEGADA) {
            chegadaDeClientes();
        }
        desistenciaAleatoria();
        alternarPostoAleatorio();
        chamarSenhas();
        exibirPainel();
    }

    /** Fim: ninguém mais chega, fila vazia e postos livres. */
    public boolean terminou() {
        return rodada >= RODADAS_COM_CHEGADA && fila.estaVazia() && postos.todosLivres();
    }

    /** Mensagens (chegadas, desistências, chamadas) geradas na última rodada executada. */
    public List<String> getMensagensRodada() {
        return mensagensRodada;
    }

    private void chegadaDeClientes() {
        int quantos = sortear(0, MAX_CHEGADAS_POR_RODADA);
        for (int i = 0; i < quantos; i++) {
            Senha senha;
            if (Math.random() < CHANCE_PRIORITARIA) {
                senha = new Senha(TipoSenha.PRIORITARIA, proximoPrioritario++);
            } else {
                senha = new Senha(TipoSenha.NORMAL, proximoNormal++);
            }
            fila.entrar(senha);
            registrarMensagem("  + Chegou cliente: retirou a senha " + senha);
        }
    }

    private void desistenciaAleatoria() {
        if (Math.random() >= CHANCE_DESISTENCIA) {
            return;
        }
        Senha desistente = fila.sortearEsperando();
        if (desistente != null && fila.desistir(desistente)) {
            desistencias++;
            registrarMensagem("  - Desistência: a senha " + desistente + " foi embora.");
        }
    }

    private void alternarPostoAleatorio() {
        if (Math.random() >= CHANCE_ALTERNAR_POSTO) {
            return;
        }
        // Sorteia entre os postos opcionais (4 ou 5)
        int numero = sortear(GerenciadorPostos.MINIMO_ATIVOS + 1, GerenciadorPostos.TOTAL_POSTOS);
        String aviso = postos.alternar(numero);
        if (aviso != null) {
            registrarMensagem("  * " + aviso);
        }
    }

    /** Enquanto houver posto livre e gente esperando, chama a próxima senha. */
    private void chamarSenhas() {
        PostoAtendimento posto = postos.primeiroDisponivel();
        while (posto != null && !fila.estaVazia()) {
            Senha senha = fila.chamar();
            posto.atender(senha, sortear(DURACAO_MIN, DURACAO_MAX));
            atendidas.empilhar(senha);
            registrarMensagem("  > Chamando senha " + senha + " -> Posto " + posto.getNumero());
            posto = postos.primeiroDisponivel();
        }
    }

    private void exibirPainel() {
        Senha[] proximas = fila.previsao(2);
        PostoAtendimento destino = postos.primeiroDisponivel();

        System.out.println();
        System.out.println("  Pessoas na fila: " + fila.tamanho()
                + " (N: " + fila.quantidadeNormais() + " | P: " + fila.quantidadePrioritarias() + ")");
        System.out.println("  Próximas senhas: " + texto(proximas[0]) + ", " + texto(proximas[1]));

        if (proximas[0] == null) {
            System.out.println("  Próxima senha vai para: (ninguém aguardando)");
        } else if (destino == null) {
            System.out.println("  Próxima senha vai para: aguarde, todos os postos ocupados");
        } else {
            System.out.println("  Próxima senha vai para: Posto " + destino.getNumero());
        }

        System.out.println("  Postos (" + postos.quantidadeAtivos() + " ativos):");
        for (PostoAtendimento posto : postos.getPostos()) {
            System.out.println("    " + posto);
        }
        System.out.println("  Atendidas: " + atendidas.tamanho() + " | Desistências: " + desistencias);
    }

    /** Mostra as atendidas da última para a primeira, desempilhando. */
    private void encerrar() {
        System.out.println("\n=== ATENDIMENTO ENCERRADO após " + rodada + " rodadas ===");
        System.out.println("Total atendidas: " + atendidas.tamanho() + " | Desistências: " + desistencias);
        System.out.println("Senhas atendidas (da última para a primeira):");

        int ordem = atendidas.tamanho();
        while (!atendidas.estaVazia()) {
            System.out.println("  " + ordem + "º atendimento: " + atendidas.desempilhar());
            ordem--;
        }
    }

    // ---- utilitários ----

    /** Guarda a mensagem na lista da rodada atual e também imprime no console. */
    private void registrarMensagem(String mensagem) {
        mensagensRodada.add(mensagem);
        System.out.println(mensagem);
    }

    /** Número inteiro aleatório entre min e max (inclusive). */
    private int sortear(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    private String texto(Senha senha) {
        return senha == null ? "---" : senha.toString();
    }

    private void pausar() {
        if (pausaMs <= 0) {
            return;
        }
        try {
            Thread.sleep(pausaMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

