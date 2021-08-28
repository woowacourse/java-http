package nextstep.jwp;

import nextstep.jwp.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

            final List<String> requestHeaders = new ArrayList<>();
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = inputStreamReader.readLine();
            while (!"".equals(line)) {
                requestHeaders.add(line);
                line = inputStreamReader.readLine();
                if (line == null) {
                    break;
                }
            }

            new HttpRequest()

            if (requestHeaders.isEmpty()) {
                throw new IllegalStateException();
            }

            final String targetResourceUrl = requestHeaders.get(0).split(" ")[1];

            if (targetResourceUrl.startsWith("/login")) {

            }

            URL resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("static" + targetResourceUrl);
            String responseBody;
            try {
                Path path = Paths.get(resource.toURI());
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            } catch (IOException | URISyntaxException exception) {
                throw new ResourceNotFoundException();
            }

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
            inputStreamReader.close();
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
