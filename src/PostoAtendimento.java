//Um guichê. Pode estar ativo ou inativo e, quando ativo, livre ou ocupado. Postos fixos (os 3 primeiros) nunca são desativados.

public class PostoAtendimento {
    private final int numero;
    private final boolean fixo;
    private boolean ativo;
    private Senha senhaAtual;       // null = livre
    private int tempoRestante;      // rodadas que faltam para terminar o atendimento

    public PostoAtendimento(int numero, boolean fixo) {
        this.numero = numero;
        this.fixo = fixo;
        this.ativo = fixo;
    }

    public void atender(Senha senha, int duracao) {
        this.senhaAtual = senha;
        this.tempoRestante = duracao;
    }

    public Senha passarRodada() {
        if (senhaAtual == null) {
            return null;
        }
        tempoRestante--;
        if (tempoRestante > 0) {
            return null;
        }
        Senha terminada = senhaAtual;
        senhaAtual = null;
        return terminada;
    }

    public boolean ativar() {
        if (ativo) {
            return false;
        }
        ativo = true;
        return true;
    }

    public boolean desativar() {
        if (fixo || !ativo || estaOcupado()) {
            return false;
        }
        ativo = false;
        return true;
    }

    public boolean estaOcupado() {
        return senhaAtual != null;
    }

    public boolean estaDisponivel() {
        return ativo && !estaOcupado();
    }

    public int getNumero() {
        return numero;
    }

    public boolean isFixo() {
        return fixo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Senha getSenhaAtual() {
        return senhaAtual;
    }

    @Override
    public String toString() {
        String status;
        if (!ativo) {
            status = "Inativo";
        } else if (estaOcupado()) {
            status = "Ocupado (" + senhaAtual + ")";
        } else {
            status = "Livre";
        }
        return "Posto " + numero + ": " + status;
    }

}
