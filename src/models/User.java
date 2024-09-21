package src.models;

public class User {
  private int user_id;
  private String email;
  private String first_name;
  private String last_name;
  private String phone_number;
  private String role;
  private String img_link;

  public User(int user_id, String email, String first_name, String last_name, String phone_number,
      String role, String img_link) {
    this.user_id = user_id;
    this.email = email;
    this.first_name = first_name;
    this.last_name = last_name;
    this.phone_number = phone_number;
    this.role = role;
    this.img_link = img_link;
  }

  // getters
  public int getUser_id() {
    return user_id;
  }

  public String getEmail() {
    return email;
  }

  public String getFirst_name() {
    return first_name;
  }

  public String getLast_name() {
    return last_name;
  }

  public String getPhone_number() {
    return phone_number;
  }

  public String getRole() {
    return role;
  }

  public String getImg_link() {
    return img_link;
  }

  // to json
  public String toJson() {
    return "{\"user_id\":" + user_id + ",\"email\":\"" + email + "\",\"first_name\":\"" + first_name
        + "\",\"last_name\":\"" + last_name + "\",\"phone_number\":\""
        + phone_number + "\",\"role\":\"" + role + "\",\"img_link\":\"" + img_link + "\"}";
  }
}
