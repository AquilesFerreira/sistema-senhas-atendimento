public class FilaPrioritaria {
    private static final int NORMAIS_POR_PRIORITARIA = 2;

    private final Fila<Senha> normais = new Fila<>();
    private final Fila<Senha> prioritarias = new Fila<>();

    private int normaisSeguidas = NORMAIS_POR_PRIORITARIA;

    public void entrar(Senha senha) {
        if (senha.isPrioritaria()) {
            prioritarias.enfileirar(senha);
        } else {
            normais.enfileirar(senha);
        }
    }

    public Senha chamar() {
        if (!prioritarias.estaVazia() && (normaisSeguidas >= NORMAIS_POR_PRIORITARIA || normais.estaVazia())) {
            normaisSeguidas = 0;
            return prioritarias.desenfileirar();
        }
        if (!normais.estaVazia()) {
            normaisSeguidas++;
            return normais.desenfileirar();
        }
        return null;
    }

    public Senha[] previsao(int quantas) {
        Senha[] resultado = new Senha[quantas];
        int contador = normaisSeguidas;
        int iN = 0;   // próxima posição a olhar na fila de normais
        int iP = 0;   // próxima posição a olhar na fila de prioritárias

        for (int i = 0; i < quantas; i++) {
            boolean temP = iP < prioritarias.tamanho();
            boolean temN = iN < normais.tamanho();

            if (temP && (contador >= NORMAIS_POR_PRIORITARIA || !temN)) {
                resultado[i] = prioritarias.pegar(iP++);
                contador = 0;
            } else if (temN) {
                resultado[i] = normais.pegar(iN++);
                contador++;
            } else {
                break;
            }
        }
        return resultado;
    }

    public boolean desistir(Senha senha) {
        return senha.isPrioritaria() ? prioritarias.remover(senha) : normais.remover(senha);
    }

    public int tamanho() {
        return normais.tamanho() + prioritarias.tamanho();
    }

    public boolean estaVazia() {
        return tamanho() == 0;
    }

    public int quantidadeNormais() {
        return normais.tamanho();
    }

    public int quantidadePrioritarias() {
        return prioritarias.tamanho();
    }

    //sorteia uma senha para simular desistencia
    public Senha sortearEsperando() {
        int total = tamanho();
        if (total == 0) {
            return null;
        }
        int sorteio = (int) (Math.random() * total);
        if (sorteio < normais.tamanho()) {
            return normais.pegar(sorteio);
        }
        return prioritarias.pegar(sorteio - normais.tamanho());
    }
}
