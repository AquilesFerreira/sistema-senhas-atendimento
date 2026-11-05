public enum TipoSenha {
    NORMAL('N'),
    PRIORITARIA('P');

    private final char letra;

    TipoSenha(char letra) {
        this.letra = letra;
    }

    public char getLetra() {
        return letra;
    }
}
