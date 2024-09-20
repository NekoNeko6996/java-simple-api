package src.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.Feedback;

public class GetFeedbackHandler implements HttpHandler {

  private List<Feedback> getFeedback() throws Exception {
    List<Feedback> data = new ArrayList<>();
    try (Statement statement = DataBase.getConnect().createStatement()) {
      String sql = "SELECT * FROM feedbacks ORDER BY create_at DESC";
      ResultSet rs = statement.executeQuery(sql);
      while (rs.next()) {
        String id = rs.getString("feedback_id");
        String user_id = rs.getString("user_id");
        String message = rs.getString("message");
        String created_at = rs.getString("create_at");

        Feedback feedback = new Feedback(Integer.parseInt(id), Integer.parseInt(user_id), message, created_at);

        data.add(feedback);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[GetFeedbackHandler][getFeedback] " + e.getMessage());
    }
    return data;
  }

  public String listToJson(List<Feedback> feedbacks) {
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
    Cors.setHeaderCORS(exchange, "application/json");

    if ("GET".equals(exchange.getRequestMethod())) {
      try {
        List<Feedback> data = getFeedback();

        String responseJSON = listToJson(data);

        exchange.sendResponseHeaders(200, 0);
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
  }
}
