package src.auth;

import src.models.AuthResult;
import src.database.DataBase;
import src.libraries.JWT;
import src.libraries.PasswordHash;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Auth {
  public static Map<String, String> getUserInfo(String email) {
    String query = "SELECT password_hash, email, user_id, first_name, last_name, phone_number, role_id, img_link FROM users WHERE email = ?";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setString(1, email);

      // hash password
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Map<String, String> data = new HashMap<>();
        data.put("email", resultSet.getString("email"));
        data.put("user_id", resultSet.getString("user_id"));
        data.put("first_name", resultSet.getString("first_name"));
        data.put("last_name", resultSet.getString("last_name"));
        data.put("phone_number", resultSet.getString("phone_number"));
        data.put("role_id", resultSet.getString("role_id"));
        data.put("img_link", resultSet.getString("img_link"));

        return data;
      }
    } catch (Exception e) {
      System.out.println("[LoginHandler][checkLogin] " + e.getMessage());
    }
    return null;
  }

  public static Map<String, String> checkLogin(String email, String password) {
    String query = "SELECT password_hash, email, user_id, first_name, last_name, phone_number, role_id FROM users WHERE email = ?";
    try (PreparedStatement statement = DataBase.getConnect().prepareStatement(query)) {
      statement.setString(1, email);

      // hash password
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        if (PasswordHash.verifyPassword(password, resultSet.getString("password_hash"))) {
          Map<String, String> data = new HashMap<>();
          data.put("email", resultSet.getString("email"));
          data.put("user_id", resultSet.getString("user_id"));
          data.put("first_name", resultSet.getString("first_name"));
          data.put("last_name", resultSet.getString("last_name"));
          data.put("phone_number", resultSet.getString("phone_number"));
          data.put("role_id", resultSet.getString("role_id"));

          return data;
        }
      }
    } catch (Exception e) {
      System.out.println("[LoginHandler][checkLogin] " + e.getMessage());
    }
    return null;
  }

  public static AuthResult check(String token) {
    try {
      if (JWT.verifyToken(token)) {
        return new AuthResult(true, "Token is valid", token, JWT.decodePayload(token));
      }
    } catch (Exception e) {
      return new AuthResult(false, "Failed to verify token", token, null);
    }
    return new AuthResult(false, "Token is invalid", token, null);
  }
}
