import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Servidor web simples, usando só com.sun.net.httpserver.HttpServer (já vem no JDK,
 * sem bibliotecas extras). Serve a página index.html e expõe a Simulacao por HTTP.
 *
 * Rotas:
 *   GET  /                      -> devolve o arquivo index.html
 *   GET  /estilo.css            -> devolve o arquivo estilo.css
 *   GET  /app.js                -> devolve o arquivo app.js
 *   GET  /estado                -> estado atual em JSON
 *   POST /rodada                -> executa uma rodada e devolve o estado em JSON
 *   POST /postos/{n}/alternar   -> liga/desliga o posto opcional n e devolve o estado em JSON
 *   POST /reiniciar             -> começa uma simulação nova e devolve o estado em JSON
 */
public class Servidor {
    private static final int PORTA = 8080;

    // Sem "final": em /reiniciar trocamos por uma Simulacao nova.
    private Simulacao simulacao = new Simulacao(0); // 0 = sem pausa, quem manda o ritmo é o front

    public static void main(String[] args) throws IOException {
        new Servidor().iniciar();
    }

    public void iniciar() throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);

        servidor.createContext("/", exchange -> tratarArquivo(exchange, "/", "index.html", "text/html; charset=UTF-8"));
        servidor.createContext("/estilo.css", exchange -> tratarArquivo(exchange, "/estilo.css", "estilo.css", "text/css"));
        servidor.createContext("/app.js", exchange -> tratarArquivo(exchange, "/app.js", "app.js", "application/javascript"));
        servidor.createContext("/estado", this::tratarEstado);
        servidor.createContext("/rodada", this::tratarRodada);
        servidor.createContext("/postos", this::tratarAlternarPosto);
        servidor.createContext("/reiniciar", this::tratarReiniciar);

        servidor.setExecutor(null); // usa a thread padrão do próprio HttpServer
        servidor.start();

        System.out.println("Servidor rodando em http://localhost:" + PORTA);
    }

    // ---- uma rota para cada caminho ----

    /** Serve um arquivo estático (index.html, estilo.css, app.js) lido da pasta do projeto. */
    private void tratarArquivo(HttpExchange exchange, String caminhoEsperado, String arquivo, String tipo) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            responderTexto(exchange, 405, "Método não permitido");
            return;
        }
        if (!caminhoEsperado.equals(exchange.getRequestURI().getPath())) {
            responderTexto(exchange, 404, "Não encontrado");
            return;
        }
        byte[] conteudo = Files.readAllBytes(Paths.get(arquivo));
        exchange.getResponseHeaders().add("Content-Type", tipo);
        exchange.sendResponseHeaders(200, conteudo.length);
        try (OutputStream corpo = exchange.getResponseBody()) {
            corpo.write(conteudo);
        }
    }

    private synchronized void tratarEstado(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            responderTexto(exchange, 405, "Método não permitido");
            return;
        }
        responderJson(exchange, simulacao.estadoJson());
    }

    private synchronized void tratarRodada(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            responderTexto(exchange, 405, "Método não permitido");
            return;
        }
        simulacao.executarRodada();
        responderJson(exchange, simulacao.estadoJson());
    }

    // Caminho esperado: /postos/{n}/alternar
    private synchronized void tratarAlternarPosto(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            responderTexto(exchange, 405, "Método não permitido");
            return;
        }
        String[] partes = exchange.getRequestURI().getPath().split("/"); // "" , "postos", "{n}", "alternar"
        if (partes.length != 4 || !"alternar".equals(partes[3])) {
            responderTexto(exchange, 404, "Rota inválida, use /postos/{n}/alternar");
            return;
        }
        int numero;
        try {
            numero = Integer.parseInt(partes[2]);
        } catch (NumberFormatException e) {
            responderTexto(exchange, 400, "Número do posto inválido");
            return;
        }
        simulacao.alternarPosto(numero);
        responderJson(exchange, simulacao.estadoJson());
    }

    private synchronized void tratarReiniciar(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            responderTexto(exchange, 405, "Método não permitido");
            return;
        }
        simulacao = new Simulacao(0);
        responderJson(exchange, simulacao.estadoJson());
    }

    // ---- utilitários de resposta ----

    private void responderJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream corpo = exchange.getResponseBody()) {
            corpo.write(bytes);
        }
    }

    private void responderTexto(HttpExchange exchange, int codigo, String texto) throws IOException {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream corpo = exchange.getResponseBody()) {
            corpo.write(bytes);
        }
    }
}
