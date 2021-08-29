package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Spring;
import jdk.dynalink.beans.StaticClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_RESOURCE = "./static";

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
            final BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            if (line == null) {
                return;
            }

            final String[] request = line.split(" ");
            final String method = request[0];
            final String uri = request[1];

            final Map<String, String> headers = new LinkedHashMap<>();
            while (!"".equals(line = reader.readLine())) {
                final String[] fields = line.split(": ");
                headers.put(fields[0], fields[1]);
            }

            URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE + uri);
            Path path = new File(resource.getFile()).toPath();

            byte[] responseBody = Files.readAllBytes(path);

            response200(outputStream, responseBody.length);
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void response200(OutputStream outputStream, int contentLength) throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + contentLength + " ");
        outputStream.write(response.getBytes());
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
