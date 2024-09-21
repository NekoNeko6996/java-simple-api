package src.handlers;

import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpExchange;

import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.models.User;

public class CheckTokenHandler implements HttpHandler {

  private User getUserInfo(int user_id) {
    String query = "SELECT users.user_id, users.email, users.first_name, users.last_name, users.phone_number, roles.role_name, users.img_link FROM users INNER JOIN roles ON users.role_id = roles.role_id WHERE users.user_id = ?;";

    try (PreparedStatement ps = DataBase.getConnect().prepareStatement(query)) {
      ps.setInt(1, user_id);
      ResultSet resultSet = ps.executeQuery();
      if (resultSet.next()) {
        User user = new User(
            resultSet.getInt("user_id"),
            resultSet.getString("email"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getString("phone_number"),
            resultSet.getString("role_name"),
            resultSet.getString("img_link"));
        return user;
      } else {
        sendResponse(null, 400, Map.of("status", "error", "message", "User not found"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String listToJson(List<User> expenses) {
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

      InputStream reqBody = exchange.getRequestBody();
      String reqBodyString = new String(reqBody.readAllBytes(), StandardCharsets.UTF_8);
      Map<String, String> reqBodyMap = JsonParser.parseJsonToMap(reqBodyString);

      // check if all fields are filled
      if (reqBodyMap.get("token") == null || reqBodyMap.get("token").isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
        return;
      }

      try {
        AuthResult result = Auth.check(reqBodyMap.get("token").toString());
        if (result.isSuccess() == false) {
          sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
          return;
        }

        int user_id = Integer.parseInt(result.getPayload().get("user_id"));
        System.out.println(user_id);

        User user = getUserInfo(user_id);

        if (user == null) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "User not found"));
          return;
        }

        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(user.toJson().getBytes(StandardCharsets.UTF_8));
        os.close();
        exchange.close();
      } catch (Exception e) {
        e.printStackTrace();
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
