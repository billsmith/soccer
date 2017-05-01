package soccer;

import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.security.Constraint;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Properties;

public class SoccerServer {
    private static final String REALM = "soccer";

    private static ConstraintSecurityHandler getSecurityHandler() {
        // add authentication
        Constraint constraint = new Constraint(Constraint.__BASIC_AUTH,"user");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[]{"user"});

        // map the security constraint to the root path.
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");

        // create the security handler, set the authentication to Basic
        // and assign the realm.
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName(REALM);
        csh.addConstraintMapping(cm);

        // set the login service
        csh.setLoginService(getHashLoginService());

        return csh;

    }
    private static HashLoginService getHashLoginService() {

        // create the login service, assign the realm and read the user credentials
        // from the file /tmp/realm.properties.
        HashLoginService hls = new HashLoginService();
        hls.setName(REALM);
        hls.setConfig("realm.properties");
        return hls;
    }

    public static void main( String[] args ) throws Exception {
        final File propertiesFile = new File(args[0]);
        if (!propertiesFile.exists()) {
            System.err.println("Properties file does not exist: "+propertiesFile.getAbsolutePath());
            System.exit(1);
        }

        final Server server = new Server(8080);

        final LoginService loginService = getHashLoginService();
        server.addBean(loginService);

        final ConstraintSecurityHandler security = getSecurityHandler();

        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { Constraint.ANY_AUTH });

        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);

        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);

        ContextHandler contextHello = new ContextHandler("/hello");
        contextHello.setContextPath("/hello");
        contextHello.setHandler(new HelloHandler());

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
        contexts.setHandlers(new Handler[] { contextHello, contextUI, contextGo });
        security.setHandler(contexts);

        server.setHandler(security);

        server.start();
        server.join();
    }
}

