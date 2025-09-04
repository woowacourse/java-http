package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpParser;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
import org.apache.coyote.util.ResourceDecodedUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String FILE_EXTENSION_DELIMITER = ".";

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
        String responseBody = createResponseBody(httpRequest, httpResponse);
        int statusCode = 200;
        String statusMessage = "OK";

        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    private String createResponseBody(final HttpRequest httpRequest, final HttpResponse httpResponse){
        String requestPath = httpRequest.requestPath();

        if (Objects.equals(requestPath, "/")) {
            requestPath = "/index.html";
        }

        if (Objects.equals(requestPath, "/login")) {
            User user = InMemoryUserRepository.getByAccountAndPassword(httpRequest.getParameterValue("account"),
                    httpRequest.getParameterValue("password"));
            log.info(user.toString());
        }

        String resourcePath = getResourcePath(requestPath);
        ResourceDecodedUrl resourceDecodedUrl = ResourceDecodedUrl.from(resourcePath);

        try {
            String responseBody = new String(Files.readAllBytes(Path.of(resourceDecodedUrl.value())));
            httpResponse.putHeader("Content-Type", getContentType(resourcePath));
            httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
            return responseBody;
        } catch (IOException e) {
            throw new RuntimeException("파일 -> 텍스트 변환 과정 실패");
        }
    }

    private String getResourcePath(final String requestPath) {
        String prefixPath = "static";
        if (requestPath.contains(FILE_EXTENSION_DELIMITER)) {
            return prefixPath + requestPath;
        }
        return prefixPath + requestPath + ".html";
    }

    private String getContentType(final String resourcePath) {
        String extension = resourcePath.substring(resourcePath.lastIndexOf(FILE_EXTENSION_DELIMITER) + 1)
                .toLowerCase();

        return switch (extension) {
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "svg" -> "image/svg+xml";
            case "html" -> "text/html";
            default -> throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        };
    }
}
