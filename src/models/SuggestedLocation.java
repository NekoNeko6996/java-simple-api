package src.models;

public class SuggestedLocation {
  private int location_id;
  private String location_name;
  private double price;
  private int expected_date;
  private String img_link;
  private String description;
  private String create_at;

  public SuggestedLocation(int location_id, String location_name, double price, int expected_date, String img_link,
      String description, String create_at) {
    this.location_id = location_id;
    this.location_name = location_name;
    this.price = price;
    this.expected_date = expected_date;
    this.img_link = img_link;
    this.description = description;
    this.create_at = create_at;
  }

  // getter
  public int getLocation_id() {
    return location_id;
  }

  public String getLocation_name() {
    return location_name;
  }

  public double getPrice() {
    return price;
  }

  public int getExpected_date() {
    return expected_date;
  }

  public String getImg_link() {
    return img_link;
  }

  public String getDescription() {
    return description;
  }

  public String getCreate_at() {
    return create_at;
  }

  // to json
  public String toJson() {
    return "{" +
        "\"location_id\":" + location_id + "," +
        "\"location_name\":\"" + location_name + "\"," +
        "\"price\":" + price + "," +
        "\"expected_date\":" + expected_date + "," +
        "\"img_link\":\"" + img_link + "\"," +
        "\"description\":\"" + description + "\"," +
        "\"create_at\":\"" + create_at + "\"" +
        "}";
  }
}
