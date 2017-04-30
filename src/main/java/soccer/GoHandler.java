package soccer;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import soccer.SendSMS;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoHandler extends AbstractHandler {

    final SendSMS sendSMS;

    public GoHandler(final SendSMS sendSMS) {
        this.sendSMS = sendSMS;
    }

    @Override
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response )
            throws IOException, ServletException {
        response.setContentType("text/text; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("OK");
        baseRequest.setHandled(true);

        final String line = request.getParameter("line");
        sendSMS.send(line);
    }
}

