package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_PATH = "static";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            RequestHeader header = header(inputStream);
            String parsedUri = header.uri();

            if ("/".equals(parsedUri)) {
                parsedUri = "/index.html";
            }
            URL resource = this.getClass().getClassLoader().getResource(DEFAULT_PATH + parsedUri);
            File file = new File(resource.toURI());
            String fileSource = new String(Files.readAllBytes(file.toPath()));

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + fileSource.getBytes().length + " ",
                    "",
                    fileSource);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private RequestHeader header(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] requestInfo = reader.readLine().split(" ");
        Map<String, String> headers = new LinkedHashMap<>();
        String line = "";

        while (!("".equals(line = reader.readLine()))) {
            String[] keyAndValue = line.split(" ");
            headers.put(keyAndValue[0], keyAndValue[1]);
        }

        return new RequestHeader(requestInfo[0], requestInfo[1], requestInfo[2], headers);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
