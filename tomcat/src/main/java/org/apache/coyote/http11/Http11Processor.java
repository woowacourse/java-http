package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RegisterHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_TYPE = "contentType";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var httpRequest = HttpRequest.from(bufferedReader);

            if (httpRequest == null) {
                return;
            }

            final var response = getResponse(httpRequest);

            if (!httpRequest.getHttpCookie().isJSessionId()) {
                // 쿠키 생성
                log.info("============쿠키 만들자!!!!!!!!!!!!!============");
                response.setCookie();
            }
            log.info("만든겨???????????????????????????????????/");

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(final HttpRequest httpRequest) throws IOException {
        return getResponse(httpRequest.getRequestUrl(), httpRequest.getRequestParams(), httpRequest.getRequestBody());
    }

    private HttpResponse getResponse(final String url, final Map<String, String> requestParam, final String requestBody)
            throws IOException {
        final Map<String, String> headers = new HashMap<>();
        if ("/".equals(url)) {
            final var responseBody = "Hello world!";
            headers.put(CONTENT_TYPE, "text/html");

            return HttpResponse.create200Response(headers, responseBody);
        }

        if ("/login".equals(url) && requestBody.isEmpty()) {
            return createStaticFileResponse(url + ".html", headers);
        }

        if ("/login".equals(url)) {
            if (LoginHandler.handle(parse(requestBody))) {
                return createUserSuccessResponse(headers);
            }

            return create401Response();
        }

        if ("/register".equals(url) && requestBody.isEmpty()) {
            return createStaticFileResponse(url + ".html", headers);
        }

        if ("/register".equals(url)) {
            if (RegisterHandler.handle(parse(requestBody))) {
                return createUserSuccessResponse(headers);
            }
            return create401Response();
        }

        if (url.contains(".")) {
            return createStaticFileResponse(url, headers);
        }

        throw new IllegalArgumentException("올바르지 않은 URL 요청입니다.");
    }

    private Map<String, String> parse(final String requestBody) {
        return Arrays.stream(requestBody.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private HttpResponse createStaticFileResponse(final String url, final Map<String, String> headers)
            throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));
        headers.put(CONTENT_TYPE, Files.probeContentType(path));

        return HttpResponse.create200Response(headers, responseBody);
    }

    private HttpResponse createUserSuccessResponse(final Map<String, String> headers) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));
        headers.put(CONTENT_TYPE, Files.probeContentType(path));

        return HttpResponse.create302Response(headers, responseBody);
    }

    private HttpResponse create401Response() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return HttpResponse.create200Response(Map.of(CONTENT_TYPE, Files.probeContentType(path)), responseBody);
    }
}
