package src;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import src.config.ConfigLoader;
import src.models.Config;
import src.handlers.*;

import java.security.SecureRandom;
import java.util.Base64;

public class Server {
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static void main(String[] args) throws IOException {
        Config config = ConfigLoader.getConfig();

        // create server
        HttpServer server = HttpServer.create(new InetSocketAddress(
                config.getServer_host(),
                config.getServer_port()),
                0);

        // handle requests
        server.createContext("/view", new WebViewHandler());
        server.createContext("/checktoken", new CheckTokenHandler());
        server.createContext("/signup", new SignUpHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/gethash", new GenerateHashStringHandler());
        server.createContext("/createtrip", new CreateATripHandler());
        server.createContext("/addtrip", new AddTripHandler());
        server.createContext("/get", new getHandler());
        server.createContext("/addcategory", new AddCategoryHandler());
        server.createContext("/addexpense", new AddExpenseHandler());

        // thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getServer_max_threads());
        server.setExecutor(executor);

        // run server
        server.start();

        System.out.println(
                "Server is listening in http://" + config.getServer_host() + ":" + server.getAddress().getPort());
    }
}
