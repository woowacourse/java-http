package nextstep.joanne.handler;

import nextstep.joanne.converter.HttpRequestResponseConverter;
import nextstep.joanne.exception.HttpException;
import nextstep.joanne.http.request.HttpRequest;
import nextstep.joanne.http.request.HttpRequestParser;
import nextstep.joanne.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            final HttpRequest httpRequest = HttpRequestResponseConverter.convertToHttpRequest(
                    new BufferedReader(
                            new InputStreamReader(inputStream)
                    )
            );

            Handler handler = new Handler(httpRequest);
            HttpResponse httpResponse;
            try {
                httpResponse = handler.handle();
            } catch (HttpException e) {
                httpResponse = ErrorHandler.handle(e.httpStatus());
            }
            outputStream.write(httpResponse.body().getBytes());
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
