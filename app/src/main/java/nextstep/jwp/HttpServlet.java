package nextstep.jwp;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class HttpServlet implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HttpServlet.class);

    private final Socket connection;

    public HttpServlet(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);

            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.service(httpRequest, httpResponse);
        } catch (IOException | ServletException exception) {
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
