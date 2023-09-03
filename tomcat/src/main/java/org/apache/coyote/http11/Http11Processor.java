package org.apache.coyote.http11;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Handler;
import org.apache.coyote.Processor;
import org.apache.coyote.common.HttpCookie;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.RequestUri;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.http11.handler.IndexHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.MethodNotAllowedHandler;
import org.apache.coyote.http11.handler.NotFoundHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.util.CookieParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private static final Map<String, Handler> HANDLER_MAP = new HashMap<>();
    private static final Handler STATIC_RESOURCE_HANDLER = new StaticResourceHandler();
    private static final Handler METHOD_NOT_ALLOWED_HANDLER = new MethodNotAllowedHandler();
    private static final Handler NOT_FOUND_HANDLER = new NotFoundHandler();

    static {
        HANDLER_MAP.put("/", new IndexHandler());
        HANDLER_MAP.put("/login", new LoginHandler());
        HANDLER_MAP.put("/register", new RegisterHandler());
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
            HttpResponse response = handle(request);
            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
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
            String key = header[0].trim();
            List<String> values = Arrays.asList(header[1].trim().split(","));
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

    private HttpResponse handle(HttpRequest request) throws IOException {
        Handler handler = HANDLER_MAP.getOrDefault(request.getPath(), STATIC_RESOURCE_HANDLER);
        try {
            return handler.handle(request);
        } catch (MethodNotAllowedException e) {
            HttpResponse response = METHOD_NOT_ALLOWED_HANDLER.handle(request);
            List<String> allowedMethods = e.getAllowedMethods().stream()
                .map(Enum::name)
                .collect(toList());
            response.setHeader("Allow", allowedMethods);
            return response;
        } catch (NoSuchFileException e) {
            return NOT_FOUND_HANDLER.handle(request);
        }
    }
}
