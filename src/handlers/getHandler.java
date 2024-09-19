package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import src.cors.Cors;

public class getHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    // send response
    exchange.sendResponseHeaders(200, 0);
    exchange.getResponseBody().close();
  }
}
