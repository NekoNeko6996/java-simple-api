package src.cors;

import com.sun.net.httpserver.HttpExchange;

public class Cors {
  public static void setHeaderCORS(HttpExchange exchange, String contentType) {
    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    exchange.getResponseHeaders().set("Content-Type", contentType);
  }
}