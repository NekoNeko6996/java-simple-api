package src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import src.config.ConfigLoader;
import src.models.Config;

public class DataBase {
  public static Connection connect = null;

  public static Connection init() {
    try {
      Config config = ConfigLoader.getConfig();

      Class.forName(config.getSql_driverClass());
      connect = DriverManager.getConnection(
          String.format("jdbc:mysql://%s:%d/%s", config.getSql_host(), config.getSql_port(), config.getSql_database()),
          config.getSql_user(),
          config.getSql_password());
      return connect;
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  // getter
  public static Connection getConnect() {
    if (connect == null) {
      connect = init();
    }
    return connect;
  }
}
