package src.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;

public class GetImageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Lấy query từ URL
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);

        String fileName = params.get("name");
        if (fileName != null) {
            // Đọc file ảnh
            File file = new File("resources/uploads/" + fileName);
            if (file.exists()) {
                exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
                exchange.sendResponseHeaders(200, file.length());

                // Gửi ảnh về client
                OutputStream os = exchange.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String response = "File not found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            String response = "Missing 'name' query parameter";
            exchange.sendResponseHeaders(400, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    result.put(keyValue[0], keyValue[1]);
                } else {
                    result.put(keyValue[0], "");
                }
            }
        }
        return result;
    }
}
