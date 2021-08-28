package nextstep.jwp;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String firstLine = bufferedReader.readLine();
            String requestUri = firstLine.split(" ")[1];

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                System.out.println(line);
            }

            if ("/login".equals(requestUri)) {
                requestUri += ".html";
            }

            final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
            final Path path = new File(resource.getPath()).toPath();
            final List<String> lines = Files.readAllLines(path);

            String result = lines.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining());


            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + result.getBytes().length + " ",
                    "",
                    result);

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
