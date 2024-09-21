package src.handlers;

import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
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

public class DeleteTimeLineHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("DELETE".equals(exchange.getRequestMethod())) {
      try {
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

        if (reqBodyMap.get("timeline_id") == null || reqBodyMap.get("timeline_id").isEmpty()
            || reqBodyMap.get("trip_id") == null || reqBodyMap.get("trip_id").isEmpty()) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Timeline ID is required"));
          return;
        }

        int timeline_id = Integer.parseInt(reqBodyMap.get("timeline_id"));
        deleteTimeline(timeline_id);
        sendResponse(exchange, 200, Map.of("status", "success", "message", "Timeline deleted successfully"));
      } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error"));
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private void deleteTimeline(int timeline_id) throws Exception {
    String query = "DELETE FROM timelines WHERE timeline_id = ?";
    PreparedStatement stmt = DataBase.getConnect().prepareStatement(query);
    stmt.setInt(1, timeline_id);
    stmt.executeUpdate();
  }

  private void sendResponse(HttpExchange exchange, int status, Map<String, String> response) throws IOException {
    exchange.sendResponseHeaders(status, 0);
    OutputStream os = exchange.getResponseBody();
    os.write(JsonParser.mapToJsonString(response).getBytes(StandardCharsets.UTF_8));
    os.close();
  }
}
