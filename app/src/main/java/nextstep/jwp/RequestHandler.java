package nextstep.jwp;

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String firstLine = bufferedReader.readLine();
            if (Objects.isNull(firstLine)) {
                return;
            }
            log.debug(firstLine);
            final String[] firstLineElements = firstLine.split(" ");
            final String httpMethod = firstLineElements[0];
            final String requestedResource = firstLineElements[1].substring(1);

            final URL resource = getClass().getClassLoader().getResource("static/" + requestedResource);
            final File file = new File(resource.getPath());
            final Path path = file.toPath();
            final byte[] bytes = Files.readAllBytes(path);

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + bytes.length + " ",
                    "",
                    "");

            outputStream.write(response.getBytes());
            outputStream.flush();
            outputStream.write(bytes);
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
