package src.models;

public class UserTable {
  private int user_id;
  private String email;
  private String password_hash;
  private String first_name;
  private String last_name;
  private String create_at;

  public UserTable(int user_id, String email, String password_hash, String first_name, String last_name, String create_at) {
    this.user_id = user_id;
    this.email = email;
    this.password_hash = password_hash;
    this.first_name = first_name;
    this.last_name = last_name;
    this.create_at = create_at;
  }

  public UserTable() {}

  // getters
  public int getUser_id() {
    return user_id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword_hash() {
    return password_hash;
  }

  public String getFirst_name() {
    return first_name;
  }

  public String getLast_name() {
    return last_name;
  }

  public String getCreate_at() {
    return create_at;
  }
}
