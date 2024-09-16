package src.views.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GetView {
  public static String readView(String filename) {
    StringBuilder content = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader("src/views/" + filename + ".html"))) {
      String line;
      while ((line = br.readLine()) != null) {
        content.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content.toString();
  }
}
