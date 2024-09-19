package src.cors;

import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.ArrayList;

public class Cors {
  public static void setHeaderCORS(HttpExchange exchange, String contentType) {

    List<String> access = new ArrayList<>();
    access.add("*");

    List<String> content = new ArrayList<>();
    content.add(contentType);

    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    exchange.getResponseHeaders().set("Content-Type", "application/json");
  }
}