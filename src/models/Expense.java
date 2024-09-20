package src.models;

public class Expense {
  private int expense_id;
  private int trip_id;
  private String expense_title;
  private double amount;
  private String category;
  private String expense_date;
  private String notes;
  private String create_at;

  public Expense(int expense_id, int trip_id, String expense_title, double amount, String category,
      String expense_date, String notes, String create_at) {
    this.expense_id = expense_id;
    this.trip_id = trip_id;
    this.expense_title = expense_title;
    this.amount = amount;
    this.category = category;
    this.expense_date = expense_date;
    this.notes = notes;
    this.create_at = create_at;
  }

  // getters
  public int getExpense_id() {
    return expense_id;
  }

  public int getTrip_id() {
    return trip_id;
  }

  public String getExpense_title() {
    return expense_title;
  }

  public double getAmount() {
    return amount;
  }

  public String getCategory() {
    return category;
  }

  public String getExpense_date() {
    return expense_date;
  }

  public String getNotes() {
    return notes;
  }

  public String getCreate_at() {
    return create_at;
  }

  // to json
  public String toJson() {
    return "{\"expense_id\":" + expense_id + ",\"trip_id\":" + trip_id + ",\"expense_title\":\"" + expense_title
        + "\",\"amount\":" + amount + ",\"category\":\"" + category + "\",\"expense_date\":\"" + expense_date
        + "\",\"notes\":\"" + notes + "\",\"create_at\":\"" + create_at + "\"}";
  }
}
