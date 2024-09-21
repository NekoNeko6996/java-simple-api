package src.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import src.cors.Cors;
import java.io.File;
import java.io.FileOutputStream;

public class ImageUploadHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equals(exchange.getRequestMethod())) {
      // Cài đặt CORS
      Cors.setHeaderCORS(exchange, "text/plain");

      InputStream inputStream = exchange.getRequestBody();

      String uniqueFileName = "file_" + System.currentTimeMillis() + ".png";

      File file = new File("/resources/uploads/" + uniqueFileName);
      FileOutputStream fileOutputStream = new FileOutputStream(file);

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        fileOutputStream.write(buffer, 0, bytesRead);
      }
      fileOutputStream.flush();
      fileOutputStream.close();
      inputStream.close();

      String response = "Image uploaded successfully!";
      exchange.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    } else {
      exchange.sendResponseHeaders(405, -1);
    }
  }
}