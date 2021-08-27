package nextstep.jwp;

import nextstep.jwp.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final HttpRequest httpRequest = HttpRequest.of(bufferedReader);
            final String responseBody = execute(httpRequest.getRequestLine());

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
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

    private String execute(String httpRequestFirstLine) {
        try {
            String method = httpRequestFirstLine.split(" ")[0];
            String url = httpRequestFirstLine.split(" ")[1];
            if (method.equalsIgnoreCase("GET")) {
                final URL resourceUrl = getClass().getResource("/static" + url);
                final String filePath = resourceUrl.getFile();

                final Path path = new File(filePath).toPath();
                return String.join("\n", Files.readAllLines(path)) + "\n";
            }
        } catch (IOException e) {
            return "Hello world!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("적절한 HTTP 헤더 포맷이 아닙니다.");
        }
        return null;
    }
}
