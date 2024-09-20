package src.models;

public class TimeLine {
  private int timeline_id;
  private int user_id;
  private int trip_id;
  private String timeline_title;
  private String timeline_content;
  private String time;
  private String create_at;

  public TimeLine(int timeline_id, int user_id, int trip_id, String timeline_title, String timeline_content,
      String time, String create_at) {
    this.timeline_id = timeline_id;
    this.user_id = user_id;
    this.trip_id = trip_id;
    this.timeline_title = timeline_title;
    this.timeline_content = timeline_content;
    this.time = time;
    this.create_at = create_at;
  }

  // getters
  public int getTimeline_id() {
    return timeline_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public int getTrip_id() {
    return trip_id;
  }

  public String getTimeline_title() {
    return timeline_title;
  }

  public String getTimeline_content() {
    return timeline_content;
  }

  public String getTime() {
    return time;
  }

  public String getCreate_at() {
    return create_at;
  }

  public String toJson() {
    return "{\"timeline_id\":" + timeline_id + ",\"user_id\":" + user_id + ",\"trip_id\":" + trip_id
        + ",\"timeline_title\":\"" + timeline_title + "\",\"timeline_content\":\"" + timeline_content
        + "\",\"time\":\"" + time + "\",\"create_at\":\"" + create_at + "\"}";
  }
}
