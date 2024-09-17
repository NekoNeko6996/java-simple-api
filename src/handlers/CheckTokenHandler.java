package src.handlers;

import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import src.cors.Cors;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import src.libraries.JWT;

public class CheckTokenHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    // get token
    Map<String, List<String>> headers = exchange.getRequestHeaders();
    String token = headers.get("Authorization").get(0).split(" ")[1];

    if (token.isEmpty()) {
      String message = "message: Token is empty";
      exchange.sendResponseHeaders(400, message.length());
      OutputStream os = exchange.getResponseBody();
      os.write(message.getBytes());
      os.close();
      exchange.close();
      return;
    }

    try {
      if (JWT.verifyToken(token)) {
        String message = "message: Token is valid";

        exchange.sendResponseHeaders(202, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
      } else {
        String message = "message: Token is invalid";
        exchange.sendResponseHeaders(401, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      String errorMessage = "message: " + e.getMessage();
      exchange.sendResponseHeaders(400, errorMessage.length());
      OutputStream os = exchange.getResponseBody();
      os.write(errorMessage.getBytes());
      os.close();
    }
    exchange.close();
  }
}
