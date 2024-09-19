package src.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.io.InputStream;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import src.libraries.*;
import java.util.HashMap;

public class SignUpHandler implements HttpHandler {
  private static final int DEFAULT_ROLE = 2;

  private static boolean save(String email, String password, String firstName, String lastName, String phone_number) {
    String query = "INSERT INTO users (email, password_hash, first_name, last_name, phone_number, role_id) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      String hashPassword = PasswordHash.hashPassword(password, PasswordHash.generateSalt());

      statement.setString(1, email);
      statement.setString(2, hashPassword);
      statement.setString(3, firstName);
      statement.setString(4, lastName);
      statement.setString(5, phone_number);
      statement.setInt(6, DEFAULT_ROLE);

      statement.executeUpdate();
      return true;
    } catch (Exception e) {
      System.out.println("[SignUpHandler][save] " + e.getMessage());
      return false;
    }
  }

  private static boolean checkEmailExists(String email) {
    String query = "SELECT user_id FROM users WHERE email = ?";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setString(1, email);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return true;
      }
    } catch (SQLException e) {
      System.out.println("[SignUpHandler][checkEmailExists] " + e.getMessage());
    }
    return false;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      OutputStream os = exchange.getResponseBody();

      // get sign up data
      Map<String, String> signUpData = JsonParser.parseJsonToMap(requestBody);

      // check if all fields are filled
      if (signUpData.get("email") == null || signUpData.get("password") == null || signUpData.get("first_name") == null
          || signUpData.get("last_name") == null || signUpData.get("phone_number") == null) {
        exchange.sendResponseHeaders(400, 0);
        return;
      }

      // check if email already exists
      if (checkEmailExists(signUpData.get("email"))) {
        String response = JsonParser.mapToJsonString(Map.of("message", "Email already exists"));
        exchange.sendResponseHeaders(409, response.length());
        os.write(response.getBytes());
        os.close();
        return;
      }

      // save data
      if (save(signUpData.get("email"), signUpData.get("password"), signUpData.get("first_name"),
          signUpData.get("last_name"), signUpData.get("phone_number"))) {
        // generate token
        try {
          Map<String, String> payload = new HashMap<>();
          payload.put("email", signUpData.get("email"));
          payload.put("first_name", signUpData.get("first_name"));
          payload.put("last_name", signUpData.get("last_name"));
          payload.put("phone_number", signUpData.get("phone_number"));
          payload.put("role_id", String.valueOf(DEFAULT_ROLE));

          String token = JWT.createToken(payload, 3600 * 24 * 7);

          Map<String, String> responseMap = Map.of(
              "message", "User created successfully",
              "token", token,
              "email", signUpData.get("email"),
              "first_name", signUpData.get("first_name"),
              "last_name", signUpData.get("last_name"),
              "phone_number", signUpData.get("phone_number"),
              "role_id", String.valueOf(DEFAULT_ROLE));

          // send response
          String response = JsonParser.mapToJsonString(responseMap);
          exchange.sendResponseHeaders(201, response.length());
          os.write(response.getBytes());
        } catch (Exception e) {
          e.printStackTrace();
          String response = JsonParser.mapToJsonString(Map.of("message", "Failed to create user"));
          exchange.sendResponseHeaders(500, response.length());
          os.write(response.getBytes());
          return;
        }
      } else {
        String response = JsonParser.mapToJsonString(Map.of("message", "Failed to create user"));
        exchange.sendResponseHeaders(500, response.length());
        os.write(response.getBytes());
      }

      os.close();
      exchange.close();
    } else {
      exchange.sendResponseHeaders(404, -1);
      return;
    }
  }
}
