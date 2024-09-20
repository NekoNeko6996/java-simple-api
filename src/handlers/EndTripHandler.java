package src.handlers;

import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import src.auth.Auth;
import src.cors.Cors;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.database.DataBase;

public class EndTripHandler implements HttpHandler {

  private boolean endTrip(int trip_id) {
    String query = "UPDATE trips SET status = 'end trip' WHERE trip_id = ?";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setInt(1, trip_id);
      statement.executeQuery();

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
      // get body
      Map<String, String> data = JsonParser.parseJsonToMap(exchange.getRequestURI().getQuery());
      if (data.get("token") == null || data.get("token").isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
        return;
      }

      // auth
      AuthResult result = Auth.check(data.get("token"));
      if (result.isSuccess() == false) {
        sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
        return;
      }

      int trip_id = Integer.parseInt(data.get("trip_id"));
      endTrip(trip_id);
    } else {
      sendResponse(exchange, 404, Map.of("status", "error", "message", "method not allowed"));
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
