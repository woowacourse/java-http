package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = createRequestMapping();

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
             final var outputStream = connection.getOutputStream();
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest request = HttpRequest.from(bufferedReader);

            RequestLine requestLine = request.getRequestLine();
            String requestPath = extractRequestPath(requestLine.getPath());

            Optional<HttpResponse> handledResponse = dispatchRequest(request);
            if (handledResponse.isPresent()) {
                outputStream.write(handledResponse.get().toString().getBytes());
                outputStream.flush();
                return;
            }

            String resourcePath = resolveResourcePath(requestPath);
            String responseBody = resolveResponseBody(resourcePath);
            if (responseBody == null) {
                HttpResponse httpResponse = HttpResponse.notFound(
                        readNotFoundPage()
                );
                outputStream.write(httpResponse.toString().getBytes());
                outputStream.flush();
            } else {
                String contentType = resolveContentType(resourcePath);
                HttpResponse httpResponse = HttpResponse.ok(
                        contentType,
                        responseBody
                );
                outputStream.write(httpResponse.toString().getBytes());
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readNotFoundPage() throws IOException {
        URL resourceUrl = getClass().getClassLoader().getResource("static/404.html");
        return readStaticResource(resourceUrl);
    }

    private Optional<HttpResponse> dispatchRequest(HttpRequest request) {
        Controller controller = requestMapping.getController(request);
        if (controller == null) {
            return Optional.empty();
        }
        HttpResponse response = new HttpResponse();
        try {
            controller.service(request, response);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
        return Optional.of(response);
    }

    private RequestMapping createRequestMapping() {
        RequestMapping mapping = new RequestMapping();
        LoginController loginController = new LoginController();
        mapping.addController("/login", loginController);
        mapping.addController("/register", new RegisterController());
        return mapping;
    }

    private String extractRequestPath(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf('?');

        if (queryStartIndex < 0) {
            queryStartIndex = requestTarget.length();
        }

        return requestTarget.substring(0, queryStartIndex);
    }

    private String resolveResourcePath(String requestPath) {
        if ("/".equals(requestPath)) {
            return "/";
        }
        if (!requestPath.contains(".")) {
            requestPath = requestPath + ".html";
        }
        return "static" + requestPath;
    }

    private String resolveResponseBody(String resourcePath) throws IOException {
        if ("/".equals(resourcePath)) {
            return "Hello world!";
        }
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        return readStaticResource(resourceUrl);
    }

    private String readStaticResource(URL resourceUrl) throws IOException {
        Path filePath = new File(resourceUrl.getPath()).toPath();
        return Files.readString(filePath);
    }

    private String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

}
