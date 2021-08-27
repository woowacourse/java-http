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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Controller controller = new Controller();

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
            String response = "";

            if (checkIfUriHasResourceExtension(httpRequest.uri())) {
                response = resolveResourceRequest(httpRequest);
            }

            if (httpRequest.uri().startsWith("/login")) {
                if ("GET".equals(httpRequest.method())) {
                    response = controller.login();
                } else if ("POST".equals(httpRequest.method())) {
                    response = controller.login(httpRequest.payload());
                }
            }

            if (httpRequest.uri().startsWith("/register")) {
                if ("GET".equals(httpRequest.method())) {
                    response = controller.register();
                } else if ("POST".equals(httpRequest.method())) {
                    response = controller.register(httpRequest.payload());
                }
            }

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

    private HttpRequest extractHttpRequest(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();

        String[] firstTokens = firstLine.split(" ");
        String method = firstTokens[0];
        String uri = firstTokens[1];

        HttpRequest httpRequest = new HttpRequest(method, uri);

        String line = reader.readLine();
        while (!"".equals(line)) {
            String[] splits = line.split(": ", 2);
            String name = splits[0];
            String value = splits[1];
            httpRequest.addHeader(name, value);
            line = reader.readLine();
        }

        if ("POST".equals(httpRequest.method()) || "PUT".equals(httpRequest.method())) {
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
