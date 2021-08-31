package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Socket connection;
    private final HandlerMapping handlerMapping;

    public RequestHandler(final Socket connection, final HandlerMapping handlerMapping) {
        this.connection = Objects.requireNonNull(connection);
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest request = requestFromReader(bufferedReader);
            final HttpResponse response = new HttpResponse();

            handlerMapping.getHandler(request).handle(request, response);

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

    private HttpRequest requestFromReader(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = requestLineFromReader(bufferedReader);
        final Headers headers = headerFromReader(bufferedReader);
        final String body = bodyFromReader(bufferedReader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private RequestLine requestLineFromReader(final BufferedReader bufferedReader) throws IOException {
        return RequestLine.of(bufferedReader.readLine());
    }

    private Headers headerFromReader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;

        while (Objects.nonNull(line = bufferedReader.readLine())) {
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }

        return Headers.of(lines);
    }

    private String bodyFromReader(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        if (!headers.hasKey(CONTENT_LENGTH)) {
            return "";
        }

        int contentLength = Integer.parseInt(headers.getValue(CONTENT_LENGTH).get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
