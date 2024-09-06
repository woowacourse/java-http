package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
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
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(input);

            boolean success = processFileRequest(request, outputStream);
            if (!success) {
                processControllerRequest(request, outputStream);
            }
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean processFileRequest(HttpRequest request, OutputStream outputStream) {
        try {
            if (request.getPath().equals("/")) {
                File welcomePage = getFile("/welcome.html");
                processFile(welcomePage, "200 OK", outputStream);
            } else {
                File requestFile = getRequestFile(request);
                processFile(requestFile, "200 OK", outputStream);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void processFile(File file, String httpStatus, OutputStream outputStream) throws IOException {
        HttpResponse response = new HttpResponse(httpStatus, file);
        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    private void processControllerRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        try {
            FrontController frontController = FrontController.getInstance();
            Controller controller = frontController.mapController(request.getMethod(), request.getPath());
            controller.service(request);
        } catch (Exception e) {
            log.warn("요청을 처리할 수 없습니다.");

            File requestFile = getFile("/404.html");
            HttpResponse response = new HttpResponse("404 NOT FOUND", requestFile);
            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        }
    }

    private File getRequestFile(HttpRequest httpRequest) {
        String resourcePath = "static" + httpRequest.getPath();
        URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (httpRequest.getPath().equals("favicon.ico")) {
            log.warn("favicon.ico 를 찾을 수 없습니다.");
        }
        return new File(resource.getPath());
    }

    private File getFile(String filePath) {
        String resourcePath = "static" + filePath;
        URL resource = getClass().getClassLoader().getResource(resourcePath);

        return new File(resource.getPath());
    }
}
