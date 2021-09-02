package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.mvc.DispatcherServlet;
import nextstep.jwp.webserver.request.DefaultHttpRequest;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.request.HttpSessions;
import nextstep.jwp.webserver.request.SessionUtil;
import nextstep.jwp.webserver.response.DefaultHttpResponse;
import nextstep.jwp.webserver.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final DispatcherServlet dispatcherServlet;
    private final HttpSessions httpSessions;

    public RequestHandler(Socket connection, DispatcherServlet dispatcherServlet, HttpSessions httpSessions) {
        this.connection = Objects.requireNonNull(connection);
        this.dispatcherServlet = dispatcherServlet;
        this.httpSessions = httpSessions;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new DefaultHttpRequest(inputStream);
            HttpResponse httpResponse = new DefaultHttpResponse(outputStream);
            httpRequest.addSessionCreator(new SessionUtil(httpSessions, httpResponse));

            dispatcherServlet.doDispatch(httpRequest, httpResponse);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
