package src.handlers;

import java.io.IOException;

import src.libraries.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.cors.Cors;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import src.libraries.JWT;

public class CreateJwtHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    // CORS
    Cors.setHeaderCORS(exchange, "application/json");
    if ("POST".equals(exchange.getRequestMethod())) {
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      if (requestBody.isEmpty()) {
        exchange.sendResponseHeaders(400, -1); // Bad request
        return;
      }

      Map<String, String> loginData = JsonParser.parseJsonToMap(requestBody);

      OutputStream os = exchange.getResponseBody();
      try {
        String jwt = JWT.createToken(loginData, 3600);

        exchange.sendResponseHeaders(200, jwt.length());
        os.write(jwt.getBytes());
      } catch (Exception e) {
        exchange.sendResponseHeaders(200, ("Error: " + e.getMessage().getBytes()).length());
        os.write(("Error: " + e.getMessage()).getBytes());
        e.printStackTrace();
      }

      os.close();
      exchange.close();
    } else {
      exchange.sendResponseHeaders(405, -1);
      exchange.close();
    }
  }
}
