package src;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import src.config.ConfigLoader;
import src.models.Config;
import src.handlers.*;

public class Server {
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
        server.createContext("/addtrip", new AddTripHandler());
        server.createContext("/addcategory", new AddCategoryHandler());
        server.createContext("/addexpense", new AddExpenseHandler());
        server.createContext("/addfeedback", new AddFeedbackHandler());

        // thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getServer_max_threads());
        server.setExecutor(executor);

        // run server
        server.start();

        System.out.println("Server is listening in http://" + config.getServer_host() + ":" + server.getAddress().getPort());
    }
}
