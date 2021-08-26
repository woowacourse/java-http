package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_PATH = "static";
    public static final String DEFAULT_METHOD = "Hello world!";

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
            final String extractedUri = extractUri(firstLine);
            List<String> lines = readLines(bufferedReader);

            if ("/".equals(extractedUri)) {
                final String response = http200Message(DEFAULT_METHOD);
                writeResponse(outputStream, response);
                return;
            }

            final URL uri = getClass().getClassLoader().getResource(STATIC_PATH + extractedUri);
            final String response = http200Message(Files.readString(new File(uri.getFile()).toPath()));

            writeResponse(outputStream, response);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private List<String> readLines(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }
        return lines;
    }

    private String extractUri(String firstLine) {
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[1];
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String http200Message(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
