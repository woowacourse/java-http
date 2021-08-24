package nextstep.jwp;

import nextstep.jwp.http.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            String line = reader.readLine();
            final RequestLine requestLine = RequestLine.of(line);

            while (!"".equals(line)) {
                line = reader.readLine();
                if (line == null) return;
            }

            final String requestUri = requestLine.getRequestUri();

            final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
            assert resource != null;
            final Path path = new File(resource.getPath()).toPath();
            final List<String> requestBody = Files.readAllLines(path);

            final StringBuilder stringBuilder = new StringBuilder();
            for (String s : requestBody) {
                stringBuilder.append(s)
                        .append("\r\n");
            }
            final String responseBody = stringBuilder.toString();

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
}
