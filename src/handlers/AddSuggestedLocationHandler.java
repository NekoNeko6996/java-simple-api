package src.handlers;

import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpHandler;

import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.AuthResult;

import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;

public class AddSuggestedLocationHandler implements HttpHandler {

  private boolean addSuggestedLocation(String location_name, double price, String expected_date,
      String img_link, String description) {

    String query = "INSERT INTO suggested_locations (location_name, price, expected_date, img_link, description) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setString(1, location_name);
      statement.setDouble(2, price);
      statement.setString(3, expected_date);
      statement.setString(4, img_link);
      statement.setString(5, description);
      statement.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // cors
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equals(exchange.getRequestMethod())) {
      try {
        // get request body
        InputStream reqBody = exchange.getRequestBody();
        String reqBodyString = new String(reqBody.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> reqBodyMap = JsonParser.parseJsonToMap(reqBodyString);
        if (reqBodyMap.get("token") == null || reqBodyMap.get("token").toString().isEmpty()) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
          return;
        }

        AuthResult result = Auth.check(reqBodyMap.get("token").toString());
        if (result.isSuccess() == false) {
          sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
          return;
        }

        String location_name = reqBodyMap.get("location_name");
        double price = Double.parseDouble(reqBodyMap.get("price"));
        String expected_date = reqBodyMap.get("expected_date");
        String img_link = reqBodyMap.get("img_link");
        String description = reqBodyMap.get("description");
        addSuggestedLocation(location_name, price, expected_date, img_link, description);

        sendResponse(exchange, 200, Map.of("status", "success", "message", "Suggested location added successfully"));
      } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, Map.of("status", "error", "message", e.getMessage()));
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
