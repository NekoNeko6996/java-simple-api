package src.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.InputStream;
import src.libraries.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.io.IOException;

public class TestHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {

    InputStream inputStream = exchange.getRequestBody();
    String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

    //
    Map<String, String> data = JsonParser.parseJsonToMap(requestBody);
    System.out.println(data);
  }
}
