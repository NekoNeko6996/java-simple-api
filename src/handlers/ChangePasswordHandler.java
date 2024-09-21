package src.handlers;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.libraries.PasswordHash;
import src.models.AuthResult;

public class ChangePasswordHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    Cors.setHeaderCORS(exchange, "application/json");

    InputStream inputStream = exchange.getRequestBody();
    String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

    Map<String, String> data = JsonParser.parseJsonToMap(requestBody);
    if (data.get("token") == null || data.get("token").isEmpty()) {
      sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
      return;
    }

    AuthResult result = Auth.check(data.get("token"));
    if (result.isSuccess() == false) {
      sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
      return;
    }

    Map<String, String> payload = result.getPayload();
    String newPassword = data.get("new_password");

    if (newPassword == null || newPassword.isEmpty()) {
      sendResponse(exchange, 400, Map.of("status", "error", "message", "Old password and new password are required"));
      return;
    }

    try {
      String query = "UPDATE users SET password_hash = ? WHERE user_id = ?";
      PreparedStatement statement = DataBase.getConnect().prepareStatement(query);

      String hashPassword = PasswordHash.hashPassword(newPassword, PasswordHash.generateSalt());

      statement.setString(1, hashPassword);
      statement.setInt(2, Integer.parseInt(payload.get("user_id")));
      int rowsUpdated = statement.executeUpdate();
      if (rowsUpdated > 0) {
        sendResponse(exchange, 200, Map.of("status", "success", "message", "Password changed successfully"));
      } else {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Failed to change password"));
      }
    } catch (Exception e) {
      sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to change password"));
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> response) throws IOException {
    String responseString = JsonParser.mapToJsonString(response);
    exchange.sendResponseHeaders(statusCode, responseString.getBytes().length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(responseString.getBytes());
    outputStream.flush();
    outputStream.close();
  }
}
