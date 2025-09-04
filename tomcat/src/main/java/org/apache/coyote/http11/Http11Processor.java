package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpParser;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
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
            HttpRequest httpRequest = HttpParser.parseToRequest(inputStream);
            log.info("HTTP 요청객체 생성 완료");

            final HttpResponse httpResponse = new HttpResponse();
            fillHttpResponse(httpRequest, httpResponse);

            outputStream.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void fillHttpResponse(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String httpVersion = httpRequest.httpVersion();
        String responseBody = createResponseBody(httpRequest);

        httpResponse.putHeader("Content-Type", getContentType(httpRequest.requestUrl()));
        httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes().length));

        int statusCode = 200;
        String statusMessage = "OK";

        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    private String createResponseBody(final HttpRequest httpRequest){
        String requestUrl = httpRequest.requestUrl();

        if (Objects.equals(requestUrl, "/")) {
            return "Hello world!";
        }

        String resourcePath = "static" + requestUrl;
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("요청한 자원을 찾을 수 없습니다.");
        }

        String decodedUrl = URLDecoder.decode(resourceUrl.getPath(), StandardCharsets.UTF_8);

        try {
            return new String(Files.readAllBytes(Path.of(decodedUrl)));
        } catch (IOException e) {
            throw new RuntimeException("파일 -> 텍스트 변환 과정 실패");
        }
    }

    private String getContentType(final String requestUrl) {
        if (Objects.equals(requestUrl, "/")) {
            return "text/html";
        }
        String extension = requestUrl.substring(requestUrl.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "svg" -> "image/svg+xml";
            case "html" -> "text/html";
            default -> throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        };
    }
}
