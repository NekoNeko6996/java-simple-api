package src.models;

public class Feedback {
  private int feedback_id;
  private int user_id;
  private String message;
  private String created_at;

  public Feedback(int feedback_id, int user_id, String message, String created_at) {
    this.feedback_id = feedback_id;
    this.user_id = user_id;
    this.message = message;
    this.created_at = created_at;
  }

  public Feedback() {
  }

  public int getFeedback_id() {
    return feedback_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public String getMessage() {
    return message;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String toJson() {
    return "{\"feedback_id\":" + feedback_id + ",\"user_id\":" + user_id + ",\"message\":\"" + message
        + "\",\"created_at\":\"" + created_at + "\"}";
  }
}
