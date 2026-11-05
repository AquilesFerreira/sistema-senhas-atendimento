//senha retirada por cliente
public class Senha {
    private final TipoSenha tipo;
    private final int numero;

    public Senha(TipoSenha tipo, int numero) {
        this.tipo = tipo;
        this.numero = numero;
    }

    public TipoSenha getTipo() {return tipo;}

    public int getNumero() {return numero;}

    public boolean isPrioritaria() {
        return tipo == TipoSenha.PRIORITARIA;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Senha)) {
            return false;
        }
        Senha outra = (Senha) objeto;
        return tipo == outra.tipo && numero == outra.numero;
    }

    @Override
    public String toString() {
        // N + número com 3 dígitos: N001, P012...
        String num = "" + numero;
        while (num.length() < 3) {
            num = "0" + num;
        }
        return tipo.getLetra() + num;
    }
}
