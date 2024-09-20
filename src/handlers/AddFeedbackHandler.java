package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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

public class AddFeedbackHandler implements HttpHandler {

  private boolean saveFeedback(int user_id, String message) {
    String sql = "INSERT INTO feedbacks (user_id, message) VALUES (?, ?)";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(sql)) {
      statement.setInt(1, user_id);
      statement.setString(2, message);
      statement.executeUpdate();
      return true;
    } catch (Exception e) {
      System.out.println("[AddFeedbackHandler][saveFeedback] " + e.getMessage());
      return false;
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      // get request body
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      try {
        // convert body to map
        Map<String, String> data = JsonParser.parseJsonToMap(requestBody);

        // check if all fields are filled
        if (data.get("message") == null) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "All fields are required"));
          return;
        }

        // check if the token is present
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

        // decode payload from token
        Map<String, String> payload = result.getPayload();
        if (!saveFeedback(Integer.parseInt(payload.get("user_id")), data.get("message"))) {
          sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to save feedback"));
          return;
        }
        sendResponse(exchange, 200, Map.of("status", "success", "message", "Feedback saved successfully"));
      } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to save feedback"));
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  // send response
  private void sendResponse(HttpExchange exchange, int code, Map<String, String> response) throws IOException {
    String responseString = JsonParser.mapToJsonString(response);
    exchange.sendResponseHeaders(code, responseString.length());
    OutputStream os = exchange.getResponseBody();
    os.write(responseString.getBytes());
    os.close();
    exchange.close();
  }
}
