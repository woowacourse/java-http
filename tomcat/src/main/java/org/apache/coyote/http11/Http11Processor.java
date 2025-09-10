package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping mapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        mapping = new RequestMapping();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.from(br);
            if (request == null) {
                return;
            }

            String path = request.getPath();

            HttpResponse response = new HttpResponse();

            if (isStatic(path)) {
                handleStatic(path, response);
                response.writeResponse(outputStream);
                return;
            }

            Controller controller = mapping.getController(request);
            if (controller != null) {
                try {
                    controller.service(request, response);

                } catch (Exception e) {
                    log.error("Controller error: {}", e.getMessage());
                    response.setStatus(500, "Internal Server Error");
                    response.writeText("Internal Server Error", "text/plain; charset=utf-8");
                }
            }
            response.writeResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isStatic(final String path) {
        return path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js");
    }

    private void handleStatic(final String path, final HttpResponse response) throws IOException, URISyntaxException {
        String cp = "static/" + (path.startsWith("/") ? path.substring(1) : path);
        URL url = getClass().getClassLoader().getResource(cp);
        if (url == null) {
            response.setStatus(404, "Not Found");
            response.writeText("Requested resource was not found on the server.", "text/plain;charset=utf-8");
            return;
        }
        Path absolutePath = Path.of(url.toURI());
        if (!Files.exists(absolutePath) || Files.isDirectory(absolutePath)) {
            response.setStatus(500, "Internal Server Error");
            response.writeText("File not found", "text/plain;charset=utf-8");
            return;
        }
        byte[] bytes = Files.readAllBytes(absolutePath);
        String contentType = guessContentType(absolutePath.toString());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setBody(bytes);
    }

    private String guessContentType(final String target) {
        if (target.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (target.endsWith(".htm")) {
            return "text/html;charset=utf-8";
        }
        if (target.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (target.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return null;
    }
}
