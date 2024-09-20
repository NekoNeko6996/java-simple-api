package src.models;

public class TripTable {
  private int trip_id;
  private int user_id;
  private String trip_name;
  private String start_date;
  private String end_date;
  private String destination;
  private double budget;
  private String currency;
  private String status;
  private boolean target;
  private String create_at;

  public TripTable(int trip_id, int user_id, String trip_name, String start_date, String end_date, String destination,
      double budget, String create_at, String currency, String status, boolean target) {
    this.trip_id = trip_id;
    this.user_id = user_id;
    this.trip_name = trip_name;
    this.start_date = start_date;
    this.end_date = end_date;
    this.destination = destination;
    this.budget = budget;
    this.create_at = create_at;
    this.currency = currency;
    this.status = status;
    this.target = target;
  }

  // getters
  public int getTrip_id() {
    return trip_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public String getTrip_name() {
    return trip_name;
  }

  public String getStart_date() {
    return start_date;
  }

  public String getEnd_date() {
    return end_date;
  }

  public String getDestination() {
    return destination;
  }

  public double getBudget() {
    return budget;
  }

  public String getCreate_at() {
    return create_at;
  }

  public String getCurrency() {
    return currency;
  }

  public String getStatus() {
    return status;
  }

  public boolean isTarget() {
    return target;
  }

  public String toJson() {
    return "{\"trip_id\":" + trip_id + ",\"user_id\":" + user_id + ",\"trip_name\":\"" + trip_name
        + "\",\"start_date\":\"" + start_date + "\",\"end_date\":\"" + end_date + "\",\"destination\":\""
        + destination + "\",\"budget\":" + budget + ",\"create_at\":\"" + create_at + "\",\"currency\":\""
        + currency + "\",\"status\":\"" + status + "\" ,\"target\":" + target + "}";
  }
}
