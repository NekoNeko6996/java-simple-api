package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JWT;
import src.libraries.JsonParser;

public class AddFeedbackHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // CORS
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
    }
  }
}
