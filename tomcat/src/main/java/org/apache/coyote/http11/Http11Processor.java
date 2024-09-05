package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String requestURL = getRequestURL(bufferedReader);

            if (requestURL.startsWith("/login?")) {
                processLoginRequest(requestURL, outputStream);
                return;
            }

            if ("/".equals(requestURL)) {
                processRootRequest(outputStream);
                return;
            }

            processRequest(requestURL, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRequestURL(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        String[] requests = firstLine.split(" ");
        return requests[1];
    }

    private void processRequest(String requestURL, OutputStream outputStream) throws URISyntaxException, IOException {
        URI resource = getClass().getClassLoader().getResource("static" + requestURL).toURI();
        final Path path = Path.of(resource);
        byte[] fileBytes = Files.readAllBytes(path);

        String contentType = requestURL.split("\\.")[1];

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + fileBytes.length + " ",
                "",
                "");

        outputStream.write(response.getBytes());
        outputStream.write(fileBytes);

        outputStream.flush();
    }

    private void processLoginRequest(String requestURL, OutputStream outputStream)
            throws URISyntaxException, IOException {
        int index = requestURL.indexOf("?");
        String queryString = requestURL.substring(index + 1);

        Map<String, String> queryParams = new HashMap<>();
        String[] querys = queryString.split("&");
        for (String query : querys) {
            String[] keyAndValue = query.split("=");
            queryParams.put(keyAndValue[0], keyAndValue[1]);
        }

        try {
            User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow();
            if (!user.checkPassword(queryParams.get("password"))) {
                throw new RuntimeException();
            }
            log.info("user: {}", user);
        } catch (Exception e) {
            processRequest("/401.html", outputStream);
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "",
                "");

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processRootRequest(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
