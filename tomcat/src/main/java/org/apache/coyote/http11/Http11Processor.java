package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.common.HttpCookie;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.RequestUri;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.exception.ResourceNotFoundException;
import org.apache.coyote.http11.controller.IndexController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.MethodNotAllowedController;
import org.apache.coyote.http11.controller.NotFoundController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.util.CookieParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, Controller> CONTROLLER_MAP = new HashMap<>();
    private static final Controller STATIC_RESOURCE_CONTROLLER = new StaticResourceController();
    private static final Controller METHOD_NOT_ALLOWED_CONTROLLER = new MethodNotAllowedController();
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();

    private final Socket connection;

    static {
        CONTROLLER_MAP.put("/", new IndexController());
        CONTROLLER_MAP.put("/login", new LoginController());
        CONTROLLER_MAP.put("/register", new RegisterController());
    }

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
        try (InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = createHttpRequest(inputStream);
            HttpResponse response = new HttpResponse();
            handle(request, response);
            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedIOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        RequestUri requestUri = getRequestUri(bufferedReader);
        HttpHeaders httpHeaders = getHttpHeaders(bufferedReader);
        HttpCookie cookie = getCookie(httpHeaders);
        String requestBody = getRequestBody(httpHeaders, bufferedReader);
        return new HttpRequest(requestUri, httpHeaders, cookie, requestBody);
    }

    private RequestUri getRequestUri(BufferedReader bufferedReader) throws IOException {
        return RequestUri.from(bufferedReader.readLine());
    }

    private HttpHeaders getHttpHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] header = line.split(":");
            String key = header[0].strip();
            List<String> values = Arrays.asList(header[1].strip().split(","));
            httpHeaders.addHeader(key, values);
        }
        return httpHeaders;
    }

    private HttpCookie getCookie(HttpHeaders httpHeaders) {
        return CookieParser.parse(httpHeaders.getHeader("Cookie"));
    }

    private String getRequestBody(HttpHeaders httpHeaders, BufferedReader bufferedReader) throws IOException {
        String header = httpHeaders.getHeader("Content-Length");
        if (header.isBlank()) {
            return "";
        }
        int contentLength = Integer.parseInt(header);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void handle(HttpRequest request, HttpResponse response) throws IOException {
        Controller controller = CONTROLLER_MAP.getOrDefault(request.getPath(), STATIC_RESOURCE_CONTROLLER);
        try {
            controller.service(request, response);
        } catch (MethodNotAllowedException e) {
            METHOD_NOT_ALLOWED_CONTROLLER.service(request, response);
            List<String> allowedMethods = e.getAllowedMethods();
            response.setHeader("Allow", allowedMethods);
        } catch (ResourceNotFoundException e) {
            NOT_FOUND_CONTROLLER.service(request, response);
        }
    }
}
