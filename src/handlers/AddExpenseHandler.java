package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.AuthResult;

import java.sql.PreparedStatement;

public class AddExpenseHandler implements HttpHandler {
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

        // Validate input data
        if (data.get("trip_id") == null || data.get("expense_title") == null || data.get("amount") == null
            || data.get("category") == null || data.get("expense_date") == null) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Missing required fields"));
          return;
        }

        // auth
        AuthResult result = Auth.check(data.get("token"));
        if (result.isSuccess() == false) {
          sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
          return;
        }

        // Save expense
        boolean status = saveExpense(
            8,
            "title",
            82,
            "category",
            "2022-01-01",
            "notes");
        if (!status) {
          sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to save expense"));
          return;
        }

        sendResponse(exchange, 200, Map.of("status", "success", "message", "Expense added successfully"));
      } catch (Exception e) {
        System.out.println("[AddExpenseHandler][handle] " + e.getMessage());
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error"));
      }
    }
    else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private boolean saveExpense(int trip_id, String expense_title, double amount, String category,
      String expense_date, String notes) {
    String query = "INSERT INTO expenses (trip_id, expense_title, amount, category, expense_date, notes) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setInt(1, trip_id);
      statement.setString(2, expense_title);
      statement.setDouble(3, amount);
      statement.setString(4, category);
      statement.setString(5, expense_date);
      statement.setString(6, notes);
      statement.executeUpdate();
      return true;
    } catch (Exception e) {
      System.out.println("[AddExpenseHandler][saveExpense] " + e.getMessage());
      return false;
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> responseMap) throws IOException {
    String response = JsonParser.mapToJsonString(responseMap);
    exchange.sendResponseHeaders(statusCode, response.length());
    exchange.getResponseBody().write(response.getBytes());
    exchange.getResponseBody().close();
  }
}
