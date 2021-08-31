package nextstep.jwp.http;

import static nextstep.jwp.http.ResourceResolver.checkIfUriHasResourceExtension;
import static nextstep.jwp.http.ResourceResolver.resolveResourceRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.web.ControllerAdvice;
import nextstep.jwp.web.controller.Controller;
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
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = extractHttpRequest(reader);

            String response = handle(httpRequest);

            log.debug("outputStream => {}", response);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IllegalStateException exception) {
            log.info("IllegalStateException {}", exception.getMessage());
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String handle(HttpRequest httpRequest) throws IOException {
        try {
            if (checkIfUriHasResourceExtension(httpRequest.uri())) {
                return resolveResourceRequest(httpRequest);
            }

            Controller controller = RequestMapper.findController(httpRequest);
            return controller.doService(httpRequest);

        } catch (Exception exception) {
            return ControllerAdvice.handle(exception);
        }
    }

    private HttpRequest extractHttpRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();

        String[] tokens = requestLine.split(" ");
        String method = tokens[0];
        String uri = tokens[1];

        HttpRequest httpRequest = new HttpRequest(method, uri);

        String line = reader.readLine();
        while (!"".equals(line)) {
            String[] splits = line.split(": ", 2);
            String name = splits[0];
            String value = splits[1];
            httpRequest.addHeader(name, value);
            line = reader.readLine();
        }

        if (httpRequest.headers().containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequest.headers().get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            httpRequest.setPayload(new String(buffer));
        }
        return httpRequest;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
