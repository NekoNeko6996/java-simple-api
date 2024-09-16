package src.models;

public class loginForm {
  private String email;
  private String password;

  // constructor
  public loginForm(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public loginForm() {
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
