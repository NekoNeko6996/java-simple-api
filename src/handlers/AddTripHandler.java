package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JWT;
import src.libraries.JsonParser;

import java.sql.PreparedStatement;

public class AddTripHandler implements HttpHandler {

  private static boolean saveNewTrip(int user_id, String trip_name, String start_date, String end_date,
      String destination, double budget) throws Exception {
    String query = "INSERT INTO trips (user_id, trip_name, start_date, end_date, destination, budget) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setInt(1, user_id);
      statement.setString(2, trip_name);
      statement.setString(3, start_date);
      statement.setString(4, end_date);
      statement.setString(5, destination);
      statement.setDouble(6, budget);
      statement.executeUpdate();
      return true;
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // Set CORS headers
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

        // Validate input data
        if (data.get("title") == null || data.get("start_date") == null || data.get("end_date") == null
            || data.get("destination") == null || data.get("budget") == null) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Missing required fields"));
          return;
        }

        // Save trip data
        saveNewTrip(
            Integer.parseInt(payload.get("user_id")),
            data.get("title"),
            data.get("start_date"),
            data.get("end_date"),
            data.get("destination"),
            Double.parseDouble(data.get("budget")));

        // Send success response
        sendResponse(exchange, 200, Map.of("status", "success", "message", "Trip added successfully"));

      } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error: " + e.getMessage()));
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method Not Allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> responseMap) throws IOException {
    String jsonResponse = JsonParser.mapToJsonString(responseMap);
    exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
    exchange.getResponseBody().write(jsonResponse.getBytes());
    exchange.getResponseBody().close();
  }
}
