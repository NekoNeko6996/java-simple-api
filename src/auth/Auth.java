package src.auth;

import src.models.AuthResult;
import src.libraries.JWT;

public class Auth {
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
