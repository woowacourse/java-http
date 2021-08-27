package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final HttpRequest httpRequest = HttpRequest.of(bufferedReader);
            final HttpResponse httpResponse = execute(httpRequest.getRequestLine());

            outputStream.write(httpResponse.responseAsBytes());
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

    private HttpResponse execute(RequestLine requestLine) {
        try {
            String method = requestLine.getMethod();
            String path = requestLine.getPath();

            if (method.equalsIgnoreCase("GET")) {
                final URL resourceUrl = getClass().getResource("/static" + path);
                final Path filePath = new File(resourceUrl.getFile()).toPath();
                final String responseBody = String.join("\n", Files.readAllLines(filePath)) + "\n";
                return HttpResponse.ok(responseBody);
            }
        } catch (IOException e) {
            return HttpResponse.ok("Hello world!");
        } catch (Exception e) {
            throw new IllegalArgumentException("적절한 HTTP 헤더 포맷이 아닙니다.");
        }

        // TODO :: ERROR TYPE, HTTP STATUS 고민
        return HttpResponse.internalServerError("");
    }
}
