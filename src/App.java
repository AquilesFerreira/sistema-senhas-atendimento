public class App {
    public static void main(String[] args) {
        // Pausa entre rodadas, em milissegundos. Ex.: java App 0 (sem pausa)
        long pausa = 800;
        if (args.length > 0) {
            pausa = Long.parseLong(args[0]);
        }
        new Simulacao(pausa).executar();
    }
}
