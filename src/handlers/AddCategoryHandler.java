package src.handlers;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.libraries.JWT;

public class AddCategoryHandler implements HttpHandler {
  private static boolean saveCategory(int user_id, String category_name) {
    String query = "INSERT INTO categories (user_id, category_name) VALUES (?, ?)";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setInt(1, user_id);
      statement.setString(2, category_name);
      statement.executeUpdate();
      return true;
    } catch (Exception e) {
      System.out.println("[AddCategoryHandler][saveCategory] " + e.getMessage());
      return false;
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      try {
        // Parse request body to Map
        Map<String, String> data = JsonParser
            .parseJsonToMap(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

        // Check if the token is present
        if (data.get("token") == null || data.get("token").isEmpty()) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
          return;
        }

        // Verify JWT token
        if (!JWT.verifyToken(data.get("token"))) {
          sendResponse(exchange, 403, Map.of("status", "error", "message", "Invalid token"));
          return;
        }

        // Decode payload from token
        Map<String, String> payload = JWT.decodePayload(data.get("token"));
        if (!saveCategory(Integer.parseInt(payload.get("user_id")), data.get("category_name"))) {
          sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to save category"));
          return;
        }
        sendResponse(exchange, 200, Map.of("status", "success", "message", "Category added successfully"));
      } catch (Exception e) {
        System.out.println("[AddCategoryHandler][handle] " + e.getMessage());
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Invalid request body"));
        return;
      }
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> responseMap) throws IOException {
    String jsonResponse = JsonParser.mapToJsonString(responseMap);
    exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
    exchange.getResponseBody().write(jsonResponse.getBytes(StandardCharsets.UTF_8));
    exchange.getResponseBody().close();
  }
}
