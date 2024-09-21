package src.handlers;

import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.AuthResult;

public class UpdateExpenseHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "application/json");

    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equals("POST")) {
      exchange.sendResponseHeaders(405, 0);
      OutputStream os = exchange.getResponseBody();
      os.write("Method not allowed".getBytes(StandardCharsets.UTF_8));
      os.close();
      return;
    }

    // Get request body
    InputStream reqBody = exchange.getRequestBody();
    String reqBodyString = new String(reqBody.readAllBytes(), StandardCharsets.UTF_8);
    Map<String, String> reqBodyMap = JsonParser.parseJsonToMap(reqBodyString);
    if (reqBodyMap.get("token") == null || reqBodyMap.get("token").isEmpty()) {
      sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
      return;
    }

    // check fields
    if (reqBodyMap.get("expense_id") == null ||
        reqBodyMap.get("expense_title") == null ||
        reqBodyMap.get("trip_id") == null ||
        reqBodyMap.get("amount") == null ||
        reqBodyMap.get("category") == null ||
        reqBodyMap.get("expense_date") == null ||
        reqBodyMap.get("notes") == null) {
      sendResponse(exchange, 400, Map.of("status", "error", "message", "All fields are required"));
      return;
    }

    // check if token is valid
    AuthResult result = Auth.check(reqBodyMap.get("token"));
    if (result.isSuccess() == false) {
      sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
      return;
    }

    try {
      int expense_id = Integer.parseInt(reqBodyMap.get("expense_id"));
      String expense_title = reqBodyMap.get("expense_title");
      int trip_id = Integer.parseInt(reqBodyMap.get("trip_id"));
      double amount = Double.parseDouble(reqBodyMap.get("amount"));
      String category = reqBodyMap.get("category");
      String expense_date = reqBodyMap.get("expense_date");
      String notes = reqBodyMap.get("notes");
      updateExpense(expense_title, trip_id, amount, category, expense_date, notes, expense_id);
      sendResponse(exchange, 200, Map.of("status", "success", "message", "Expense updated successfully"));
    } catch (Exception e) {
      e.printStackTrace();
      sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to update expense"));
    }
  }

  private void updateExpense(String expense_title, int trip_id, double amount, String category, String expense_date,
      String notes, int expense_id) {
    try {
      PreparedStatement ps = DataBase
          .getConnect()
          .prepareStatement(
              "UPDATE expenses SET expense_title = ?, amount = ?, category = ?, expense_date = ?, notes = ? WHERE expense_id = ? AND trip_id = ?;");
      ps.setString(1, expense_title);
      ps.setDouble(2, amount);
      ps.setString(3, category);
      ps.setString(4, expense_date);
      ps.setString(5, notes);
      ps.setInt(6, expense_id);
      ps.setInt(7, trip_id);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendResponse(HttpExchange exchange, int status, Map<String, String> response) throws IOException {
    exchange.sendResponseHeaders(status, 0);
    OutputStream os = exchange.getResponseBody();
    os.write(JsonParser.mapToJsonString(response).getBytes(StandardCharsets.UTF_8));
    os.close();
  }
}
