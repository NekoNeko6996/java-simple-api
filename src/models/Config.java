package src.models;

public class Config {
  private String server_host;
  private int server_port;
  private int server_max_threads;
  private String sql_host;
  private int sql_port;
  private String sql_user;
  private String sql_password;
  private String sql_database;
  private String sql_driverClass;
  private String auth_secret_key;

  public Config() {
  }

  public Config(String server_host, int server_port, int server_max_threads, String sql_host, int sql_port,
      String sql_user, String sql_password, String sql_database, String sql_driverClass, String auth_secret_key) {
    this.server_host = server_host;
    this.server_port = server_port;
    this.server_max_threads = server_max_threads;
    this.sql_host = sql_host;
    this.sql_port = sql_port;
    this.sql_user = sql_user;
    this.sql_password = sql_password;
    this.sql_database = sql_database;
    this.sql_driverClass = sql_driverClass;
    this.auth_secret_key = auth_secret_key;
  }

  // getters
  public String getAuth_secret_key() {
    return auth_secret_key;
  }
  public String getServer_host() {
    return server_host;
  }

  public int getServer_port() {
    return server_port;
  }

  public int getServer_max_threads() {
    return server_max_threads;
  }

  public String getSql_host() {
    return sql_host;
  }

  public int getSql_port() {
    return sql_port;
  }

  public String getSql_user() {
    return sql_user;
  }

  public String getSql_password() {
    return sql_password;
  }

  public String getSql_database() {
    return sql_database;
  }

  public String getSql_driverClass() {
    return sql_driverClass;
  }

  // setters
  public void setAuth_secret_key(String auth_secret_key) {
    this.auth_secret_key = auth_secret_key;
  }

  public void setServer_host(String server_host) {
    this.server_host = server_host;
  }

  public void setServer_port(int server_port) {
    this.server_port = server_port;
  }

  public void setServer_max_threads(int server_max_threads) {
    this.server_max_threads = server_max_threads;
  }

  public void setSql_host(String sql_host) {
    this.sql_host = sql_host;
  }

  public void setSql_port(int sql_port) {
    this.sql_port = sql_port;
  }

  public void setSql_user(String sql_user) {
    this.sql_user = sql_user;
  }

  public void setSql_password(String sql_password) {
    this.sql_password = sql_password;
  }

  public void setSql_database(String sql_database) {
    this.sql_database = sql_database;
  }

  public void setSql_driverClass(String sql_driverClass) {
    this.sql_driverClass = sql_driverClass;
  }

}
