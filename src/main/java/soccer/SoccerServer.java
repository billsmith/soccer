package soccer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class SoccerServer {

    public static void main( String[] args ) throws Exception {
        final File propertiesFile = new File(args[0]);
        if (!propertiesFile.exists()) {
            System.err.println("Properties file does not exist: "+propertiesFile.getAbsolutePath());
            System.exit(1);
        }

        Server server = new Server(8080);

        ContextHandler context = new ContextHandler("/hello");
        context.setContextPath("/hello");
        context.setHandler(new HelloHandler());

        final Properties properties = new Properties();
        properties.load(new FileReader(propertiesFile));
        final String accountSID = properties.getProperty("accountSID");
        final String authToken = properties.getProperty("authToken");
        final String phoneTo = properties.getProperty("phoneTo");
        final String phoneFrom = properties.getProperty("phoneFrom");

        final SendSMS sendSMS = new SendSMS(accountSID, authToken, phoneTo, phoneFrom);

        ContextHandler contextGo = new ContextHandler("/go");
        contextGo.setContextPath("/go");
        contextGo.setHandler(new GoHandler(sendSMS));

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
        resourceHandler.setResourceBase("content");

        ContextHandler contextUI = new ContextHandler("/");
        contextUI.setHandler(resourceHandler);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, contextUI, contextGo });
        server.setHandler(contexts);

        server.start();
        server.join();
    }
}

