package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;

import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.models.Expense;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetExpenseHandler implements HttpHandler {

  private List<Expense> getExpense(int trip_id) {
    List<Expense> expenses = new ArrayList<>();

    try {
      String sql = "SELECT * FROM expenses WHERE trip_id = ? ORDER BY expense_date ASC;";
      PreparedStatement ps = DataBase.getConnect().prepareStatement(sql);
      ps.setInt(1, trip_id);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Expense expense = new Expense(
            rs.getInt("expense_id"),
            rs.getInt("trip_id"),
            rs.getString("expense_title"),
            rs.getDouble("amount"),
            rs.getString("expense_date"),
            rs.getString("create_at"),
            rs.getString("category"),
            rs.getString("notes"));
        expenses.add(expense);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return expenses;
  }

  public String listToJson(List<Expense> expenses) {
    StringBuilder jsonArray = new StringBuilder();
    jsonArray.append("[");

    for (int i = 0; i < expenses.size(); i++) {
      jsonArray.append(expenses.get(i).toJson());
      if (i < expenses.size() - 1) {
        jsonArray.append(",");
      }
    }

    jsonArray.append("]");
    return jsonArray.toString();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equals(exchange.getRequestMethod())) {

      // get body
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      // convert body to map
      Map<String, String> data = JsonParser.parseJsonToMap(requestBody);
      if (data.get("token") == null || data.get("token").isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
        return;
      }

      //
      if (data.get("trip_id") == null || data.get("trip_id").isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "trip_id is required"));
        return;
      }

      // auth
      AuthResult result = Auth.check(data.get("token"));
      if (result.isSuccess() == false) {
        sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
        return;
      }

      try {
        int trip_id = Integer.parseInt(data.get("trip_id"));

        List<Expense> expenses = getExpense(trip_id);
        System.out.println(expenses);

        if (expenses == null) {
          sendResponse(exchange, 404, Map.of("status", "error", "message", "Expense not found"));
          return;
        }
        System.out.println(expenses);

        String responseJSON = listToJson(expenses);
        exchange.sendResponseHeaders(200, responseJSON.length());
        OutputStream os = exchange.getResponseBody();
        os.write(responseJSON.getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        sendResponse(exchange, 500, Map.of("status", "error", "message", e.getMessage()));
        e.printStackTrace();
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int status, Map<String, String> data) throws IOException {
    String responseJSON = JsonParser.mapToJsonString(data);
    exchange.sendResponseHeaders(status, responseJSON.length());
    OutputStream os = exchange.getResponseBody();
    os.write(responseJSON.getBytes(StandardCharsets.UTF_8));
    os.close();
  }
}
