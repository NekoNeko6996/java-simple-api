package src.models;

public class expenseTable {
  private int expense_id;
  private int user_id;
  private int trip_id;
  private double amount;
  private String category;
  private String expense_date;
  private String notes;
  private String create_at;

  public expenseTable(int expense_id, int user_id, int trip_id, double amount, String category, String expense_date, String notes, String create_at) {
    this.expense_id = expense_id;
    this.user_id = user_id;
    this.trip_id = trip_id;
    this.amount = amount;
    this.category = category;
    this.expense_date = expense_date;
    this.notes = notes;
    this.create_at = create_at;
  }

  public expenseTable() {}

  // getters
  public int getExpense_id() {
    return expense_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public int getTrip_id() {
    return trip_id;
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
}
