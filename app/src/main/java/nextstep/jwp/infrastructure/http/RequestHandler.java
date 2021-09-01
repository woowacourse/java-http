package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.WebApplicationContext;
import nextstep.jwp.infrastructure.http.interceptor.HandlerInterceptor;
import nextstep.jwp.infrastructure.http.reader.HttpRequestReader;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HandlerMapping handlerMapping;
    private final HandlerInterceptor interceptor;

    public RequestHandler(final Socket connection, final WebApplicationContext context) {
        this.connection = Objects.requireNonNull(connection);
        this.handlerMapping = context.getHandlerMapping();
        this.interceptor = context.getInterceptor();
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequestReader httpRequestReader = new HttpRequestReader(bufferedReader);
            final HttpRequest request = httpRequestReader.readHttpRequest();
            final HttpResponse response = new HttpResponse();

            interceptor.preHandle(request, response);
            handlerMapping.getHandler(request).handle(request, response);
            interceptor.postHandle(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IllegalArgumentException exception) {
            log.error("Exception occurs", exception);
        } catch (Exception exception) {
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
