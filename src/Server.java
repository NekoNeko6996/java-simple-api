package src;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import src.config.ConfigLoader;  
import src.models.Config;
import src.handlers.*;

public class Server {
    static Connection connect(String dbUrl, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = java.sql.DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connection established");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        Config config = ConfigLoader.getConfig();

        // create server
        HttpServer server = HttpServer.create(new InetSocketAddress(
                config.getServer_host(),
                config.getServer_port()),
                0);

        // handle requests
        server.createContext("/adduser", new AddUserHandler());
        server.createContext("/view", new WebViewHandler());
        server.createContext("/createjwt", new CreateJwtHandler());
        server.createContext("/checktoken", new CheckTokenHandler());
        server.createContext("/signup", new SignUpHandler());

        // thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getServer_max_threads());
        server.setExecutor(executor);

        // run server
        server.start();

        System.out.println("Server is listening in http://" + config.getServer_host() + ":" + server.getAddress().getPort());
    }
}
