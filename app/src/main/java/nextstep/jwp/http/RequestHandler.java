package nextstep.jwp.http;

import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.exception.ExceptionHandler;
import nextstep.jwp.http.mapper.ControllerMapper;
import nextstep.jwp.http.mapper.ExceptionHandlerMapper;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpTranslator httpTranslator = new HttpTranslator(inputStream, outputStream);
            HttpRequestMessage httpRequestMessage = httpTranslator.translate();

            log.debug("요청 Uri: {}", httpRequestMessage.requestUri());

            HttpResponseMessage httpResponseMessage = handle(httpRequestMessage);
            httpTranslator.respond(httpResponseMessage);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception exception) {
            log.error("Exception", exception);
        } finally {
            close();
        }
    }

    private HttpResponseMessage handle(HttpRequestMessage httpRequestMessage) {
        try {
            String requestUri = httpRequestMessage.requestUri();
            Controller controller = ControllerMapper.getInstance().resolve(requestUri);
            log.debug("서비스 시작 {}", controller.getClass().getName());
            return controller.service(httpRequestMessage);
        } catch (RuntimeException exception) {
            ExceptionHandler exceptionHandler = ExceptionHandlerMapper.getInstance().resolve(exception);
            log.debug("예외 핸들링 {}", exception.getMessage());
            return exceptionHandler.run(exception);
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
