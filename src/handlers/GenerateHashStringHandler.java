package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import src.cors.Cors;
import src.libraries.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

public class GenerateHashStringHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "application/json");
  
    // get body
    InputStream inputStream = exchange.getRequestBody();
    String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

    String salt = Salt.generateSalt();
    System.out.println("[GenerateHashStringHandler][handle] Salt: " + salt);

    // send response
    exchange.sendResponseHeaders(200, 0);
    exchange.getResponseBody().close();
  }
}
  