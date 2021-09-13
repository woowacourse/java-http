package nextstep.jwp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ExceptionHandler;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.PageNotFoundException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.HttpRequestReader;
import nextstep.jwp.utils.HttpResponseWriter;
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

            HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());
            final HttpResponseWriter httpResponseWriter = new HttpResponseWriter(outputStream);

            ContentType contentType = ContentType.findBy(httpRequest.getUri());
            if (!contentType.isNone() && !contentType.isHtml()) {
                log.debug(httpRequest.getUri());
                responseResource(httpRequest, httpResponse, contentType);
                httpResponseWriter.writeHttpResponse(httpResponse);
                return;
            }

            proceed(httpRequest, httpResponse);
            httpResponseWriter.writeHttpResponse(httpResponse);
        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void proceed(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Controller controller = RequestMapper.map(httpRequest);
        try {
            if (controller == null) {
                throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
            }
            controller.service(httpRequest, httpResponse);
        } catch (MethodNotAllowedException e) {
            ExceptionHandler.methodNotAllowed(httpResponse);
        } catch (NotFoundException e) {
            ExceptionHandler.notFound(httpResponse);
        } catch (Exception e) {
            ExceptionHandler.internalServerError(httpResponse);
        }
    }

    private void responseResource(
            HttpRequest httpRequest,
            HttpResponse httpResponse,
            ContentType contentType
    ) throws Exception {
        final String resource = FileReader.file(httpRequest.getUri());
        httpResponse.setHttpStatus(HttpStatus.OK);

        httpResponse.addHeaders("Content-Type", contentType.getContentType());
        httpResponse.addHeaders("Content-Length", String.valueOf(resource.getBytes().length));

        httpResponse.setBody(resource);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
