package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import src.cors.Cors;

public class CreateATripHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    exchange.getRequestHeaders().put("Access-Control-Allow-Origin", "*");
    exchange.getRequestHeaders().put("Content-type", "application/json");


    exchange.sendResponseHeaders(200, 0);
    exchange.getResponseBody().close();
  }
}
