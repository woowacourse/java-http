package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort());

        try (
            final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()
        ) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String firstLine = bufferedReader.readLine();
            String[] parsingResult = firstLine.split(" ");
            String requestURI = parsingResult[1];

            String filename = "static/index.html";
            if (!isRoot(requestURI)) {
                String requestPath = requestURI.substring(1);
                if (hasExtension(requestPath)) {
                    filename = "static/" + requestPath;
                }
                if (!hasExtension(requestPath)) {
                    filename = "static/" + requestPath + ".html";
                }
            }

            URL url = getClass().getClassLoader().getResource(filename);
            File file = new File(url.getPath());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(firstLine)
                .append("\r\n");

            String line;
            while (!"".equals(line = bufferedReader.readLine())) {
                if (line == null) {
                    return;
                }
                stringBuilder.append(line)
                    .append("\r\n");
            }

            String responseBody = new String(Files.readAllBytes(file.toPath()));
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

    private boolean isRoot(String requestURI) {
        return "/".equals(requestURI);
    }

    private boolean hasExtension(String requestPath) {
        return requestPath.contains(".");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
