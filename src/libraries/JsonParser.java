package src.libraries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
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

  public static <T> String convertToJsonString(T object) {
    Class<?> clazz = object.getClass();
    StringBuilder json = new StringBuilder();
    json.append("{");

    Field[] fields = clazz.getDeclaredFields();
    boolean firstField = true;

    for (Field field : fields) {
      field.setAccessible(true);
      try {
        if (!firstField) {
          json.append(",");
        }
        firstField = false;

        String name = field.getName();
        Object value = field.get(object);

        json.append("\"").append(name).append("\":");

        if (value == null) {
          json.append("null");
        } else if (value instanceof String) {
          json.append("\"").append(value).append("\"");
        } else if (value instanceof Number || value instanceof Boolean) {
          json.append(value);
        } else {
          json.append("\"").append(value.toString()).append("\"");
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    json.append("}");
    return json.toString();
  }

  public static String mapToJsonString(Map<String, String> map) {
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("{");

    int size = map.size();
    int count = 0;

    for (Map.Entry<String, String> entry : map.entrySet()) {
      jsonBuilder.append("\"").append(entry.getKey()).append("\":")
          .append("\"").append(entry.getValue()).append("\"");

      count++;
      if (count < size) {
        jsonBuilder.append(",");
      }
    }

    jsonBuilder.append("}");
    return jsonBuilder.toString();
  }

  
}
