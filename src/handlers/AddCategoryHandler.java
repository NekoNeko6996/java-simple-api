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
import src.models.AuthResult;
import src.auth.Auth;

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

        // Validate input data
        if (data.get("category_name") == null || data.get("category_name").isEmpty() || data.get("token") == null) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Missing required fields"));
          return;
        }

        // auth
        AuthResult result = Auth.check(data.get("token"));
        if (result.isSuccess() == false) {
          sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
          return;
        }

        Map<String, String> payload = result.getPayload();

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
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> responseMap) throws IOException {
    String jsonResponse = JsonParser.mapToJsonString(responseMap);
    exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
    exchange.getResponseBody().write(jsonResponse.getBytes(StandardCharsets.UTF_8));
    exchange.getResponseBody().close();
  }
}
