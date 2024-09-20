package src.models;

import java.util.Map;

public class AuthResult {
  private boolean isSuccess;
  private String message;
  private String token;
  private Map<String, String> payload;

  public AuthResult(boolean isSuccess, String message, String token, Map<String, String> payload) {
    this.isSuccess = isSuccess;
    this.message = message;
    this.token = token;
    this.payload = payload;
  }

  public AuthResult() {
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public String getMessage() {
    return message;
  }

  public String getToken() {
    return token;
  }

  public Map<String, String> getPayload() {
    return payload;
  }
}
