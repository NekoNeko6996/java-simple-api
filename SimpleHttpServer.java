import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        // create server
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // handle requests
        server.createContext("/api", new MyHandler());
        server.createContext("/public", new PublicHandler());

        // run server
        server.setExecutor(null);
        server.start();

        System.out.println("Server is listening on port " + server.getAddress().getPort());
    }

    // CORS
    static void setHeaderCORS(HttpExchange exchange, String contentType) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Content-type", contentType);
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // CORS
            setHeaderCORS(exchange, "application/json");

            // response data
            String response = "{\"message\":\"Hello from Java Plain!\"}";

            // send response
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());

            // close stream
            os.close();
        }
    }

    static class PublicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setHeaderCORS(exchange, "text/html");

            String response = readFile("views/index.html");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
