package src.handlers;

import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.sql.PreparedStatement;
import java.util.Map;
import src.auth.Auth;
import src.cors.Cors;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.database.DataBase;

public class EndTripHandler implements HttpHandler {

  private boolean endTrip(int trip_id) {
    String query = "UPDATE trips SET status = ? WHERE trip_id = ?";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setString(1, "completed");
      statement.setInt(2, trip_id);
      statement.executeUpdate();

      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equals(exchange.getRequestMethod())) {
      // Get body (not from query)
      String requestBody = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
      Map<String, String> data = JsonParser.parseJsonToMap(requestBody);

      if (data.get("token") == null || data.get("token").isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
        return;
      }

      // auth
      AuthResult result = Auth.check(data.get("token"));
      if (!result.isSuccess()) {
        sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
        return;
      }

      // Validate trip_id
      if (data.get("trip_id") == null || data.get("trip_id").isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Trip ID is required"));
        return;
      }

      int trip_id;
      try {
        trip_id = Integer.parseInt(data.get("trip_id"));
      } catch (NumberFormatException e) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Invalid trip ID"));
        return;
      }

      // End trip
      if (endTrip(trip_id)) {
        sendResponse(exchange, 200, Map.of("status", "success"));
      } else {
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to end trip"));
      }
    } else {
      sendResponse(exchange, 404, Map.of("status", "error", "message", "method not allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> responseMap) throws IOException {
    String jsonResponse = JsonParser.mapToJsonString(responseMap);
    exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
    exchange.getResponseBody().write(jsonResponse.getBytes());
    exchange.getResponseBody().close();
  }
}
