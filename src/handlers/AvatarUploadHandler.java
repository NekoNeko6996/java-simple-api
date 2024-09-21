package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import src.auth.Auth;
import src.database.DataBase;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.config.ConfigLoader;

public class AvatarUploadHandler implements HttpHandler {

    private void updateAvatar(int user_id, String img_link) throws Exception {
        String query = "UPDATE users SET img_link = ? WHERE user_id = ?";
        PreparedStatement statement = DataBase.getConnect().prepareStatement(query);
        statement.setString(1, img_link);
        statement.setInt(2, user_id);
        statement.executeUpdate();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Allow CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            // Pre-flight request; just send a 200 response
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if ("POST".equals(exchange.getRequestMethod())) {
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);

            InputStream inputStream = exchange.getRequestBody();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            String body = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
            String[] parts = body.split("--" + boundary);

            String fileName = null;
            String jsonData = null;

            for (String part : parts) {
                if (part.contains("filename=\"")) {
                    // Extract the filename
                    fileName = part.substring(part.indexOf("filename=\"") + 10);
                    fileName = fileName.substring(0, fileName.indexOf("\""));
                    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                    fileName = "file_" + System.currentTimeMillis() + "." + extension;

                    // Extract file content
                    String fileData = part.substring(part.indexOf("\r\n\r\n") + 4, part.lastIndexOf("\r\n"));
                    // Save the file
                    File file = new File("resources/uploads/" + fileName);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        fileOutputStream.write(fileData.getBytes(StandardCharsets.ISO_8859_1));
                    }

                } else if (part.contains("Content-Disposition: form-data; name=\"json\"")) {
                    // Extract JSON data
                    jsonData = part.substring(part.indexOf("\r\n\r\n") + 4);
                    jsonData = jsonData.substring(0, jsonData.lastIndexOf("\r\n"));
                }
            }

            if (fileName != null && jsonData != null) {
                // Parse the JSON to extract the user_id
                Map<String, String> jsonObject = JsonParser.parseJsonToMap(jsonData);
                AuthResult result = Auth.check(jsonObject.get("token"));
                if (!result.isSuccess()) {
                    sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
                    return;
                }

                int user_id = Integer.parseInt(result.getPayload().get("user_id"));
                // Update the avatar link in the database
                try {
                    String img_link = ConfigLoader.getConfig().getServer_host() + ":" + ConfigLoader.getConfig().getServer_port()
                        + "/getimage?name=" + fileName;
                    updateAvatar(user_id, img_link);
                    // Return the new image link to the client
                    String response = JsonParser.mapToJsonString(Map.of("img_link", img_link));
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                    return;
                }
            } else {
                exchange.sendResponseHeaders(400, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void sendResponse(HttpExchange exchange, int code, Map<String, String> response) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(JsonParser.mapToJsonString(response).getBytes());
        os.close();
        exchange.close();
    }
}
