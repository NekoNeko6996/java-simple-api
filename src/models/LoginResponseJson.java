package src.models;

public class LoginResponseJson {
  private String token;

  public LoginResponseJson(String token) {
    this.token = token;
  }

  public LoginResponseJson() {
    
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
