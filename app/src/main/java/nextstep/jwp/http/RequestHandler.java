package nextstep.jwp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ExceptionHandler;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.HttpRequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestReader.httpRequest(inputStream);
            log.debug("Request : {} {}", httpRequest.getMethod(), httpRequest.getUri());
            log.debug("Requested Body : {}", httpRequest.getBody());

            HttpResponse httpResponse = new HttpResponse(outputStream);

            if (httpRequest.getCookie().getCookies("JSESSIONID") == null) {
                httpResponse.addHeaders("Set-Cookie", String.format("JSESSIONID=%s", UUID.randomUUID()));
            }

            ContentType contentType = ContentType.findBy(httpRequest.getUri());
            if (!contentType.isNone() && !contentType.isHtml()) {
                log.debug(httpRequest.getUri());
                responseResource(httpRequest, httpResponse, contentType);
                return;
            }

            Controller controller = RequestMapper.map(httpRequest);
            if (controller == null) {
                ExceptionHandler.notFound(httpResponse);
                return;
            }
            controller.service(httpRequest, httpResponse);

        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void responseResource(
            HttpRequest httpRequest,
            HttpResponse httpResponse,
            ContentType contentType
    ) throws IOException {
        final String resource = httpRequest.getUri();
        httpResponse.addHeaders("Content-Type", contentType.getContentType());
        httpResponse.addHeaders("Content-Length", String.valueOf(resource.getBytes().length));

        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeHeaders();
        httpResponse.writeBody(FileReader.file(resource));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
