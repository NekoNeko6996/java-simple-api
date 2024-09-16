package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import src.cors.Cors;
import src.database.DataBase;
import src.libraries.JsonParser;
import src.models.loginForm;

public class AddUserHandler implements HttpHandler {
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

      loginForm loginData = JsonParser.fromJson(requestBody, loginForm.class);
      //
      String response;
      String query = "INSERT INTO users (email, password) VALUES (?, ?)";

      try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
        statement.setString(1, loginData.getEmail());
        statement.setString(2, loginData.getPassword());
        statement.executeUpdate();

        response = "User added successfully!";
        System.out.println("[AddUserHandler][add user]:");
        System.out.println("[Username]: " + loginData.getEmail());
        System.out.println("[Password]: " + loginData.getPassword());
      } catch (SQLException e) {
        e.printStackTrace();
        response = "Error: " + e.getMessage();
      }

      exchange.sendResponseHeaders(200, response.length());
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
      exchange.close();
    } else {
      exchange.sendResponseHeaders(405, -1); // Method not allowed
    }
  }
}
