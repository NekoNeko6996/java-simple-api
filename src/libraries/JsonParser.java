package src.libraries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JsonParser {

  // convert json to object
  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      // create instance
      T instance = clazz.getDeclaredConstructor().newInstance();

      // convert json to map key and value
      Map<String, String> jsonMap = parseJsonToMap(json);

      for (Field field : clazz.getDeclaredFields()) {
        // access to private field
        field.setAccessible(true);

        // get value
        String value = jsonMap.get(field.getName());

        if (value != null) {
          // int value
          if (field.getType() == int.class) {
            field.setInt(instance, Integer.parseInt(value));
          }
          // double value
          else if (field.getType() == double.class) {
            field.setDouble(instance, Double.parseDouble(value));
          }
          // boolean value
          else if (field.getType() == boolean.class) {
            field.setBoolean(instance, Boolean.parseBoolean(value));
          }
          // string value
          else {
            field.set(instance, value);
          }
        }
      }

      // return result object
      return instance;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // convert json string to map key and value
  public static Map<String, String> parseJsonToMap(String json) {
    Map<String, String> map = new HashMap<>();
    // delete curly braces
    json = json.trim().replaceAll("\\{", "").replaceAll("\\}", "");

    // cut json lines
    String[] pairs = json.split(",");
    for (String pair : pairs) {
      // cut key and value
      String[] keyValue = pair.split(":");

      // delete backspace, double quote
      String key = keyValue[0].trim().replaceAll("\"", ""); // Xử lý key
      String value = keyValue[1].trim().replaceAll("\"", ""); // Xử lý value
      map.put(key, value);
    }
    return map;
  }
}
