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
        server.createContext("/addtimeline", new AddTimelineHandler());
        server.createContext("/addsuggestedlocation", new AddSuggestedLocationHandler());

        // edit
        server.createContext("/endtrip", new EndTripHandler());

        // get route
        server.createContext("/getfeedback", new GetFeedbackHandler());
        server.createContext("/getsuggestedlocation", new GetSuggestedLocationHandler());
        server.createContext("/gettrip", new GetTripHandler());
        server.createContext("/getontrip", new GetOnTripHandler());
        server.createContext("/getexpense", new GetExpenseHandler());
        server.createContext("/gettimeline", new GetTimelineHandler());
        server.createContext("/gettriphistory", new GetTripHistoryHandler());
        server.createContext("/getuserinfo", new CheckTokenHandler());
        server.createContext("/getimage", new GetImageHandler());

        // update route
        server.createContext("/updateuserinfo", new UpdateUserInfoHandler());
        server.createContext("/updateexpense", new UpdateExpenseHandler());
        server.createContext("/changepassword", new ChangePasswordHandler());

        // delete route
        server.createContext("/deleteexpense", new DeleteExpenseHandler());
        server.createContext("/deletetimeline", new DeleteTimeLineHandler());

        //
        server.createContext("/test", new TestHandler());

        // upload resources
        server.createContext("/uploadimg", new ImageUploadHandler());
        server.createContext("/uploadavatar", new AvatarUploadHandler());

        // thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getServer_max_threads());
        server.setExecutor(executor);

        // run server
        server.start();

        System.out.println(
                "Server is listening in http://" + config.getServer_host() + ":" + server.getAddress().getPort());
    }
}
