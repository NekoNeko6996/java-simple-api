package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.auth.Auth;
import src.cors.Cors;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.models.TimeLine;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import src.database.DataBase;
import java.util.ArrayList;

public class GetTimelineHandler implements HttpHandler {

  private List<TimeLine> getTimeline(int trip_id) {
    List<TimeLine> timelines = new ArrayList<TimeLine>();

    try {
      String sql = "SELECT * FROM timelines WHERE trip_id = ? ORDER BY timeline_id DESC";

      PreparedStatement ps = DataBase.getConnect().prepareStatement(sql);
      ps.setInt(1, trip_id);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        TimeLine timeline = new TimeLine(
            rs.getInt("timeline_id"),
            rs.getInt("user_id"),
            rs.getInt("trip_id"),
            rs.getString("timeline_title"),
            rs.getString("timeline_content"),
            rs.getString("time"),
            rs.getString("create_at"));
        timelines.add(timeline);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return timelines;
  }

  public String listToJson(List<TimeLine> timeLines) {
    StringBuilder jsonArray = new StringBuilder();
    jsonArray.append("[");

    for (int i = 0; i < timeLines.size(); i++) {
      jsonArray.append(timeLines.get(i).toJson());
      if (i < timeLines.size() - 1) {
        jsonArray.append(",");
      }
    }

    jsonArray.append("]");
    return jsonArray.toString();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equals(exchange.getRequestMethod())) {

      // get body
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      try {
        // parse body
        Map<String, String> data = JsonParser.parseJsonToMap(requestBody);
        if (data.get("token") == null || data.get("token").isEmpty()) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
          return;
        }

        // validate input data
        if (data.get("trip_id") == null || data.get("trip_id").isEmpty()) {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "trip_id is required"));
          return;
        }

        // auth
        AuthResult result = Auth.check(data.get("token"));
        if (result.isSuccess() == false) {
          sendResponse(exchange, 403, Map.of("status", "error", "message", result.getMessage()));
          return;
        }

        // get timeline
        List<TimeLine> timelines = getTimeline(Integer.parseInt(data.get("trip_id")));
        String responseJSON = listToJson(timelines);

        // send response
        exchange.sendResponseHeaders(200, responseJSON.length());
        OutputStream os = exchange.getResponseBody();
        os.write(responseJSON.getBytes(StandardCharsets.UTF_8));
        os.close();
      } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error"));
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int status, Map<String, String> response) throws IOException {
    String responseJSON = JsonParser.mapToJsonString(response);
    exchange.sendResponseHeaders(status, responseJSON.length());
    OutputStream os = exchange.getResponseBody();
    os.write(responseJSON.getBytes());
    os.close();
  }
}
