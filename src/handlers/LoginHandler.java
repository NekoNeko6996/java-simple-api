package src.handlers;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.cors.Cors;
import src.database.DataBase;
import src.libraries.*;
import src.models.loginForm;

import java.sql.SQLException;

public class LoginHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Cors.setHeaderCORS(exchange, "application/json");

    if ("POST".equals(exchange.getRequestMethod())) {
      InputStream inputStream = exchange.getRequestBody();
      String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      loginForm loginData = JsonParser.fromJson(requestBody, loginForm.class);

      String query = "SELECT * FROM users WHERE email = ? AND password = ?";
      try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {

        statement.setString(1, loginData.getEmail());
        statement.setString(2, loginData.getPassword());
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
          System.out.println(resultSet.toString());
        }
      } catch (SQLException e) {
        System.out.println("[LoginHandler][handle] " + e.getMessage());
      }
    }
  }
}
