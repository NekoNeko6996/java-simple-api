package src.cors;

import com.sun.net.httpserver.HttpExchange;

public class Cors {
  public static void setHeaderCORS(HttpExchange exchange, String contentType) {

    exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().set("Content-type", contentType);

  }
}