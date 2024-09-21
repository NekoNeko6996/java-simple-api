package src.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JWT;
import src.libraries.JsonParser;
import src.models.AuthResult;

public class UpdateUserInfoHandler implements HttpHandler {
  private boolean updateUser(int user_id, String first_name, String last_name, String email, String phone_number,
      String img_link)
      throws Exception {
    String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, img_link = ? WHERE user_id = ?;";
    try (PreparedStatement ps = DataBase.getConnect().prepareStatement(query)) {
      ps.setString(1, first_name);
      ps.setString(2, last_name);
      ps.setString(3, email);
      ps.setString(4, phone_number);
      ps.setString(5, img_link);
      ps.setInt(6, user_id);
      ps.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equals("POST")) {
      exchange.sendResponseHeaders(405, 0);
      OutputStream os = exchange.getResponseBody();
      os.write("Method not allowed".getBytes());
      os.close();
      return;
    }

    InputStream reqBody = exchange.getRequestBody();
    String reqBodyString = new String(reqBody.readAllBytes(), StandardCharsets.UTF_8);
    Map<String, String> reqBodyMap = JsonParser.parseJsonToMap(reqBodyString);
    if (reqBodyMap.get("token") == null || reqBodyMap.get("token").isEmpty()) {
      sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
      return;
    }

    AuthResult result = Auth.check(reqBodyMap.get("token"));
    if (result.isSuccess() == false) {
      sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
      return;
    }

    System.out.println(reqBodyMap);

    if (reqBodyMap.get("email") == null || reqBodyMap.get("email").isEmpty() ||
        reqBodyMap.get("phone_number") == null || reqBodyMap.get("phone_number").isEmpty() ||
        reqBodyMap.get("image_link") == null || reqBodyMap.get("image_link").isEmpty() ||
        reqBodyMap.get("first_name") == null || reqBodyMap.get("first_name").isEmpty() ||
        reqBodyMap.get("last_name") == null || reqBodyMap.get("last_name").isEmpty()) {
      sendResponse(exchange, 400, Map.of("status", "error", "message", "All fields are required"));
      return;
    }

    int user_id = Integer.parseInt(result.getPayload().get("user_id"));
    String email = reqBodyMap.get("email");
    String phone_number = reqBodyMap.get("phone_number");
    String image_link = "https://" + reqBodyMap.get("image_link");
    String first_name = reqBodyMap.get("first_name");
    String last_name = reqBodyMap.get("last_name");

    try {
      if (updateUser(user_id, first_name, last_name, email, phone_number,
          image_link)) {
        Map<String, String> newUserInfo = Auth.getUserInfo(email);
        newUserInfo.put("token", JWT.createToken(newUserInfo, 3600 * 24 * 30));

        sendResponse(exchange, 200, newUserInfo);
      } else {
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to update user"));
      }
    } catch (Exception e) {
      e.printStackTrace();
      sendResponse(exchange, 500, Map.of("status", "error", "message", "Failed to update user"));
    }

    sendResponse(exchange, 200, Map.of("status", "success", "message", "User updated successfully"));
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
