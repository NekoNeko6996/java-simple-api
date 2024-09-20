package src.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import src.cors.Cors;
import src.models.SuggestedLocation;
import src.database.DataBase;
import src.libraries.JsonParser;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetSuggestedLocationHandler implements HttpHandler {

  public String listToJson(List<SuggestedLocation> feedbacks) {
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

  private List<SuggestedLocation> getSuggestedLocations() throws Exception {
    List<SuggestedLocation> data = new ArrayList<>();
    try (Statement statement = DataBase.getConnect().createStatement()) {
      String sql = "SELECT * FROM suggested_locations;";
      ResultSet rs = statement.executeQuery(sql);
      while (rs.next()) {
        int location_id = rs.getInt("location_id");
        String location_name = rs.getString("location_name");
        double price = rs.getDouble("price");
        int expected_date = rs.getInt("expected_date");
        String img_link = rs.getString("img_link");
        String description = rs.getString("description");
        String create_at = rs.getString("create_at");

        SuggestedLocation suggestedLocation = new SuggestedLocation(location_id, location_name, price, expected_date,
            img_link, description, create_at);
        data.add(suggestedLocation);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[GetTripHandler][getSuggestedLocations] " + e.getMessage());
    }
    return data;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("GET".equals(exchange.getRequestMethod())) {
      try {
        List<SuggestedLocation> data = getSuggestedLocations();

        String responseJSON = listToJson(data);

        exchange.sendResponseHeaders(200, responseJSON.length());
        OutputStream os = exchange.getResponseBody();
        os.write(responseJSON.getBytes());
        os.close();
      } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, Map.of("status", "error", "message", "Internal server error"));
      }
    } else {
      sendResponse(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
    }
  }

  private void sendResponse(HttpExchange exchange, int code, Map<String, String> response) throws IOException {
    exchange.sendResponseHeaders(code, 0);
    OutputStream os = exchange.getResponseBody();
    os.write(JsonParser.mapToJsonString(response).getBytes());
    os.close();
    exchange.close();
  }
}
