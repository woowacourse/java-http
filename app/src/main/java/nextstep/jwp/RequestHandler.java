package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final int FIRST_LINE_OF_HTTP_REQUEST = 0;
    private static final String BLANK_DELIMITER = " ";
    private static final int SECOND_WORD_INDEX = 1;

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            List<String> httpRequest = extractHttpRequest(inputStream);
            String requestURI = extractRequestURI(httpRequest);
            String responseBody = readStaticFile(requestURI);

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

    private List<String> extractHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> request = new ArrayList<>();
        while (reader.ready()) {
            request.add(reader.readLine());
        }
        return Collections.unmodifiableList(request);
    }

    private String extractRequestURI(List<String> httpRequest) {
        String firstLine = httpRequest.get(FIRST_LINE_OF_HTTP_REQUEST);
        String requestURI = firstLine.split(BLANK_DELIMITER)[SECOND_WORD_INDEX];
        return requestURI;
    }

    private String readStaticFile(String requestURI) throws IOException {
        String fileName = "static" + requestURI;

        URL resource = getClass().getClassLoader().getResource(fileName);
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
