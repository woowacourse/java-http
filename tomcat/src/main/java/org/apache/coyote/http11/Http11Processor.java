package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.service.UserService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> requestLines = getInput(reader);

            HttpRequest request = new HttpRequest(requestLines);

            if (request.getMethod().equals("GET") && request.getPath().equals("/login")) {
                UserService.checkUser(
                        request.getQueryParameter("account"),
                        request.getQueryParameter("password")
                );
            }

            String contentType = parseContentType(request.getPath());
            String body = getResponseBody(request.getPath());
            response(body, contentType, outputStream);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getInput(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    private String parseContentType(String uri) {
        if (uri.endsWith(".css")) {
            return "text/css;charset=utf-8";
        } else if (uri.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        } else {
            return "text/html;charset=utf-8";
        }
    }

    private String getResponseBody(String path) throws IOException, URISyntaxException {
        URL resource = getResource(path);
        if (resource == null || Files.isDirectory(Path.of(resource.toURI()))) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL getResource(String path) {
        StringBuilder targetPath = new StringBuilder("static");

        if (path.equals("/login")) {
            targetPath.append("/login.html");
        } else {
            targetPath.append(path);
        }

        return getClass()
                .getClassLoader()
                .getResource(targetPath.toString());
    }

    private void response(String body, String contentType, OutputStream outputStream) throws IOException {
        final var response = createHttpResponse(body, contentType);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String createHttpResponse(String body, String contentType) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
