package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.status.HttpStatus;
import nextstep.jwp.mapping.ExceptionHandler;
import nextstep.jwp.mapping.Handler;
import nextstep.jwp.mapping.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HandlerMapping handlerMapping;

    public RequestHandler(Socket connection, ApplicationContext applicationContext) {
        this.connection = Objects.requireNonNull(connection);
        this.handlerMapping = new HandlerMapping(applicationContext);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            HttpRequest httpRequest = httpRequestParser(bufferedReader);

            log.debug("http path: " + httpRequest.getPath());

            HttpResponse httpResponse = new HttpResponse();

            handle(httpRequest, httpResponse);

            String response = httpResponse.responseMessage();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        try {
            Handler mappedHandler = handlerMapping.getHandler(httpRequest);
            mappedHandler.handle(httpRequest, httpResponse);
        } catch (HttpException e) {
            ExceptionHandler.handle(e.getHttpStatus(), httpRequest, httpResponse);
        } catch (RuntimeException e) {
            ExceptionHandler.handle(HttpStatus.INTERNAL_SERVER_ERROR, httpRequest, httpResponse);
        }
    }

    private HttpRequest httpRequestParser(BufferedReader bufferedReader) throws IOException {
        return HttpRequest.create(bufferedReader);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
