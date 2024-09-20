package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.libraries.*;
import src.cors.Cors;
import src.database.DataBase;

public class LoginHandler implements HttpHandler {

  // create login
  private static Map<String, String> checkLogin(String email, String password) {
    String query = "SELECT password_hash, email, user_id, first_name, last_name, phone_number, role_id FROM users WHERE email = ?";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setString(1, email);

      // hash password
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        if (PasswordHash.verifyPassword(password, resultSet.getString("password_hash"))) {
          Map<String, String> data = new HashMap<>();
          data.put("email", resultSet.getString("email"));
          data.put("user_id", resultSet.getString("user_id"));
          data.put("first_name", resultSet.getString("first_name"));
          data.put("last_name", resultSet.getString("last_name"));
          data.put("phone_number", resultSet.getString("phone_number"));
          data.put("role_id", resultSet.getString("role_id"));

          return data;
        }
      }
    } catch (Exception e) {
      System.out.println("[LoginHandler][checkLogin] " + e.getMessage());
    }
    return null;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    System.out.println("[LoginHandler][handle] Method: " + exchange.getRequestMethod());

    if ("POST".equals(exchange.getRequestMethod())) {
      // get request body
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      // convert body to map
      Map<String, String> loginData = JsonParser.parseJsonToMap(requestBody);

      // check if all fields are filled
      if (loginData.get("email") == null || loginData.get("password") == null) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "All fields are required"));
        return;
      }

      // check login
      try {
        Map<String, String> data = checkLogin(loginData.get("email"), loginData.get("password"));
        if (data != null) {
          data.put("token", JWT.createToken(data, 3600));
          sendResponse(exchange, 200, data);
        } else {
          sendResponse(exchange, 401, Map.of("status", "error", "message", "Invalid email or password"));
        }
      } catch (Exception e) {
        System.out.println("[LoginHandler][handle] " + e.getMessage());
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error"));
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int code, Map<String, String> response) throws IOException {
    String responseString = JsonParser.mapToJsonString(response);
    exchange.sendResponseHeaders(code, responseString.length());
    OutputStream os = exchange.getResponseBody();
    os.write(responseString.getBytes());
    os.close();
    exchange.close();
  }
}
