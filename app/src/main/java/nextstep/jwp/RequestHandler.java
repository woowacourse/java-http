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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Controller controller = new Controller();

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                final OutputStream outputStream = connection.getOutputStream()) {

            String firstLine = reader.readLine();
            log.debug("first line = {}", firstLine);

            if (firstLine == null) {
                return;
            }
            String[] firstTokens = firstLine.split(" ");
            String method = firstTokens[0];
            String uri = firstTokens[1];

            Map<String, String> headers = new HashMap<>();


            String line = reader.readLine();
            while (!"".equals(line)) {
                String[] splits = line.split(": ", 2);
                String key = splits[0];
                String value = splits[1];
                headers.put(key, value);
                line = reader.readLine();
                log.debug("Headers=> {}: {}", splits[0], splits[1]);
            }

            String response = "";

            if (uri.equals("/index.html")) {
                response = controller.index();
            }

            if (uri.startsWith("/login")) {
                if ("GET".equals(method)) {
                    response = controller.login();
                }
                else if("POST".equals(method)) {
                    String requestBody = getRequestBody(reader, headers);
                    response = controller.login(requestBody);
                }
            }

            if (uri.startsWith("/register")) {
                if ("GET".equals(method)) {
                    response = controller.register();
                }
                else if("POST".equals(method)) {
                    String requestBody = getRequestBody(reader, headers);
                    response = controller.register(requestBody);
                }
            }

            if (uri.endsWith(".css") && "GET".equals(method)) {
                response = controller.css(uri);
            }

            if (uri.endsWith(".js") && "GET".equals(method)) {
                response = controller.js(uri);
            }

            log.debug("outputStream => {}", response);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getRequestBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        return requestBody;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
