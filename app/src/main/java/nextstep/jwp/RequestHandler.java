package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.http.HttpInputStreamReader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.tomcat.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;
    private final ServletContainer servletContainer = new ServletContainer();

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpInputStreamReader httpInputStreamReader = new HttpInputStreamReader(inputStream);
            HttpRequest httpRequest = httpInputStreamReader.readHttpRequest();
            HttpResponse httpResponse = new HttpResponse();

            servletContainer.process(httpRequest, httpResponse);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
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
