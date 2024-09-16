package src.config;

import src.models.Config;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import src.libraries.JsonParser;

public class ConfigLoader {
  private static Config config = null;

  public static Config load() {
    InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.json");
    if (inputStream == null) {
      System.out.println("[ConfigLoader][getConfig] Config file not found.");
      return null;
    }

    try {
      String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      config = JsonParser.fromJson(json, Config.class);
      return config;
    } catch (Exception e) {
      System.out.println("[ConfigLoader][getConfig] " + e.getMessage());
      return null;
    }
  }

  // getter
  public static Config getConfig() {
    if (config == null) {
      load();
    }
    return config;
  }
}
