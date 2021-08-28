package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            String responseBody = "Hello world!";

            HttpRequest httpRequest = new HttpRequest(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            );

            if (httpRequest.getUrl().equals("/")) {
                responseBody = "Hello world!";
            } else if (httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
                String url = Objects.requireNonNull(getClass().getClassLoader().getResource("static/" + httpRequest.getUrl())).getPath();
                Path path = Paths.get(url);
                responseBody = new String(Files.readAllBytes(path));
            }

            String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            response(outputStream, response);

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void response(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
