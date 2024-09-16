package src.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.cors.Cors;
import src.views.controllers.GetView;

public class WebViewHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "text/html");

    String html = GetView.readView("home");
    byte[] responseBytes = html.getBytes("UTF-8");
    exchange.sendResponseHeaders(200, responseBytes.length);

    try (OutputStream os = exchange.getResponseBody()) {
      os.write(responseBytes);
    }

    exchange.close();
  }
}