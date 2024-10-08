package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import src.auth.Auth;
import src.cors.Cors;
import java.nio.charset.StandardCharsets;
import src.database.DataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import src.libraries.JsonParser;
import src.models.AuthResult;
import src.models.TripTable;

public class GetTripHandler implements HttpHandler {

  private List<TripTable> getTrip(int user_id) throws Exception {
    List<TripTable> trips = new ArrayList<>();
    String query = "SELECT * FROM trips WHERE user_id = ?;";
    try (PreparedStatement stmt = DataBase.getConnect().prepareStatement(query)) {
      stmt.setInt(1, user_id);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        TripTable trip = new TripTable(
            rs.getInt("trip_id"),
            user_id,
            rs.getString("trip_name"),
            rs.getString("start_date"),
            rs.getString("end_date"),
            rs.getString("destination"),
            rs.getDouble("budget"),
            rs.getString("create_at"),
            rs.getString("currency"),
            rs.getString("status"),
            rs.getBoolean("target"));
        trips.add(trip);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[GetTripHandler][getTrip] " + e.getMessage());
    }
    return trips;
  }

  public String listToJson(List<TripTable> feedbacks) {
    StringBuilder jsonArray = new StringBuilder();
    jsonArray.append("[");

    for (int i = 0; i < feedbacks.size(); i++) {
      jsonArray.append(feedbacks.get(i).toJson());
      if (i < feedbacks.size() - 1) {
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

    if ("GET".equals(exchange.getRequestMethod())) {

      // get body
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      // convert body to map
      Map<String, String> data = JsonParser.parseJsonToMap(requestBody);
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

      try {
        int user_id = Integer.parseInt(result.getPayload().get("user_id"));
        List<TripTable> trips = getTrip(user_id);
        String responseJSON = listToJson(trips);

        // response
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

  private void sendResponse(HttpExchange exchange, int status, Map<String, String> data) throws IOException {
    String responseJSON = JsonParser.mapToJsonString(data);
    exchange.sendResponseHeaders(status, responseJSON.length());
    OutputStream os = exchange.getResponseBody();
    os.write(responseJSON.getBytes(StandardCharsets.UTF_8));
    os.close();
  }
}
