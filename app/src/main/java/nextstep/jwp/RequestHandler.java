package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
            String responseBody;
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String firstLine = bufferedReader.readLine();
            if (firstLine != null) {
                List<String> requestHeader = Arrays.asList(firstLine.split(" "));
                if (requestHeader.get(0).equals("GET")) {
                    String fileName = requestHeader.get(1);
                    final URL url = getClass().getClassLoader().getResource("static" + fileName);
                    if (url != null) {
                        final Path path = new File(url.getPath()).toPath();
                        responseBody = Files.readString(path);
                        final String response = String.join("\r\n",
                                "HTTP/1.1 200 OK ",
                                "Content-Type: text/html;charset=utf-8 ",
                                "Content-Length: " + responseBody.getBytes().length + " ",
                                "",
                                responseBody);
                        outputStream.write(response.getBytes());
                        outputStream.flush();
                    }
                }
            }
            log.info(firstLine);
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
