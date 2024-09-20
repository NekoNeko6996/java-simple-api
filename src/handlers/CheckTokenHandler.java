package src.handlers;

import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import src.cors.Cors;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import src.libraries.JWT;
import src.libraries.JsonParser;

public class CheckTokenHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equals(exchange.getRequestMethod())) {
      // get token
      Map<String, List<String>> headers = exchange.getRequestHeaders();
      String token = headers.get("Authorization").get(0).split(" ")[1];

      if (token.isEmpty()) {
        sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is required"));
        return;
      }

      try {
        if (JWT.verifyToken(token)) {
          sendResponse(exchange, 200, Map.of("status", "success", "message", "Token is valid"));
        } else {
          sendResponse(exchange, 400, Map.of("status", "error", "message", "Token is invalid"));
          return;
        }
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
