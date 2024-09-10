package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
            HttpResponse response = new HttpResponse();

            boolean success = processFileRequest(request, response);
            if (!success) {
                processControllerRequest(request, response);
            }

            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean processFileRequest(HttpRequest request, HttpResponse response) {
        try {
            if (request.getPath().equals("/")) {
                processBody("Hello world!", "200 OK", response);
            } else {
                try {
                    File requestFile = getRequestFile(request);
                    processBody(requestFile, "200 OK", response);
                } catch (NullPointerException e) {
                    log.warn("존재하지 않는 파일 호출 (Path: %s)".formatted(request.getPath()));
                    return false;
                } catch (IOException e) {
                    log.warn("파일 읽기/쓰기 과정에서 예외 발생 (Path: %s)".formatted(request.getPath()));
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void processBody(File file, String httpStatus, HttpResponse response) throws IOException {
        response = new HttpResponse(httpStatus, file);
    }

    private void processBody(String body, String httpStatus, HttpResponse response) throws IOException {
        response = new HttpResponse(httpStatus, body);
    }

    private void processControllerRequest(HttpRequest request, HttpResponse response) throws IOException {
        try {
            FrontController frontController = FrontController.getInstance();
            Controller controller = frontController.mapController(request.getMethod(), request.getPath());
            controller.service(request);
        } catch (Exception e) {
            log.warn("요청을 처리할 수 없습니다.");
            processNotFoundPage(response);
        }
    }

    private void processNotFoundPage(HttpResponse response) throws IOException {
        File file = getFile("/404.html");
        processBody(file, "404 NOT FOUND", response);
    }

    private File getRequestFile(HttpRequest httpRequest) throws NullPointerException {
        String resourcePath = "static" + httpRequest.getPath();

        URL resource = getClass().getClassLoader().getResource(resourcePath);

        return new File(resource.getPath());
    }

    private File getFile(String filePath) throws NullPointerException {
        String resourcePath = "static" + filePath;
        URL resource = getClass().getClassLoader().getResource(resourcePath);

        File file = new File(resource.getPath());
        return file;
    }
}
