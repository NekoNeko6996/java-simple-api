package src.models;

public class TripTable {
  private int trip_id;
  private int user_id;
  private String trip_name;
  private String start_date;
  private String end_date;
  private String destination;
  private double budget;
  private String create_at;

  public TripTable(int trip_id, int user_id, String trip_name, String start_date, String end_date, String destination, double budget, String create_at) {
    this.trip_id = trip_id;
    this.user_id = user_id;
    this.trip_name = trip_name;
    this.start_date = start_date;
    this.end_date = end_date;
    this.destination = destination;
    this.budget = budget;
    this.create_at = create_at;
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
}
