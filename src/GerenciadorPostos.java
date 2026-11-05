public class GerenciadorPostos {
    public static final int TOTAL_POSTOS = 5;
    public static final int MINIMO_ATIVOS = 3;

    private final PostoAtendimento[] postos = new PostoAtendimento[TOTAL_POSTOS];

    public GerenciadorPostos() {
        for (int i = 0; i < TOTAL_POSTOS; i++) {
            boolean fixo = i < MINIMO_ATIVOS;
            postos[i] = new PostoAtendimento(i + 1, fixo);
        }
    }

    //O primeiro posto ativo e livre ou null se todos estiverem ocupados
    public PostoAtendimento primeiroDisponivel() {
        for (PostoAtendimento posto : postos) {
            if (posto.estaDisponivel()) {
                return posto;
            }
        }
        return null;
    }

    // Passa uma rodada em todos os postos e devolve quantos terminaram
    public int passarRodada() {
        int terminados = 0;
        for (PostoAtendimento posto : postos) {
            if (posto.passarRodada() != null) {
                terminados++;
            }
        }
        return terminados;
    }

    //Liga ou desliga um posto opcional (4 ou 5) Devolve o que aconteceu
    public String alternar(int numero) {
        PostoAtendimento posto = buscar(numero);
        if (posto == null || posto.isFixo()) {
            return null;
        }
        if (!posto.isAtivo()) {
            posto.ativar();
            return "Posto " + numero + " foi ATIVADO.";
        }
        if (posto.desativar()) {
            return "Posto " + numero + " foi DESATIVADO.";
        }
        return null;
    }

    public PostoAtendimento buscar(int numero) {
        if (numero < 1 || numero > TOTAL_POSTOS) {
            return null;
        }
        return postos[numero - 1];
    }

    public boolean todosLivres() {
        for (PostoAtendimento posto : postos) {
            if (posto.estaOcupado()) {
                return false;
            }
        }
        return true;
    }

    public int quantidadeAtivos() {
        int ativos = 0;
        for (PostoAtendimento posto : postos) {
            if (posto.isAtivo()) {
                ativos++;
            }
        }
        return ativos;
    }

    public PostoAtendimento[] getPostos() {
        return postos;
    }
}
