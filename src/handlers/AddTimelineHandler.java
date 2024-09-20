package src.handlers;

import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import src.auth.Auth;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.AuthResult;

public class AddTimelineHandler implements HttpHandler {

  private boolean saveTimeline(int user_id, int trip_id, String timeline_title, String timeline_content, String time) {
    String sql = "INSERT INTO timelines (user_id, trip_id, timeline_title, timeline_content, time) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement ps = DataBase.getConnect().prepareStatement(sql)) {
      ps.setInt(1, user_id);
      ps.setInt(2, trip_id);
      ps.setString(3, timeline_title);
      ps.setString(4, timeline_content);
      ps.setString(5, time);
      ps.executeUpdate();
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
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      // convert body to map
      Map<String, String> requestBodyMap = JsonParser.parseJsonToMap(requestBody);
      if (requestBodyMap.get("token") == null || requestBodyMap.get("token").toString().isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
        return;
      }

      // auth
      AuthResult result = Auth.check(requestBodyMap.get("token").toString());
      if (result.isSuccess() == false) {
        sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
        return;
      }

      try {
        // get user_id
        int user_id = Integer.parseInt(result.getPayload().get("user_id"));
        int trip_id = Integer.parseInt(requestBodyMap.get("trip_id"));

        // save timeline
        String timeline_title = requestBodyMap.get("timeline_title");
        String timeline_content = requestBodyMap.get("timeline_content");
        String time = requestBodyMap.get("time");

        if (saveTimeline(user_id, trip_id, timeline_title, timeline_content, time)) {
          System.out.println("[AddTimelineHandler][handle] Timeline added successfully");
          sendResponse(exchange, 200, Map.of("status", "success", "message", "Timeline added successfully"));
        } else {
          System.out.println("[AddTimelineHandler][handle] Timeline not added");
          sendResponse(exchange, 500, Map.of("status", "error", "message", "Timeline not added"));
        }

      } catch (Exception e) {
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error"));
        e.printStackTrace();
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
