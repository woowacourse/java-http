package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.apache.coyote.TomcatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;
    private final TomcatController tomcatController;

    public Http11Processor(Socket connection,
                           HttpRequestParser httpRequestParser,
                           TomcatController tomcatController) {
        this.connection = connection;
        this.httpRequestParser = httpRequestParser;
        this.tomcatController = tomcatController;
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

            final HttpRequest httpRequest = httpRequestParser.parse(inputStream);
            log.info("httpRequest : {}", httpRequest);
            tomcatController.handleRequest(httpRequest);
            final var response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest) {
        try {
            if (isRootPath(httpRequest)) {
                final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                return buildSuccessResponse(ContentType.HTML, body);
            }
            final String staticFilePath = getStaticFilePath(httpRequest);
            final byte[] body = readFile(staticFilePath);
            final var contentType = ContentType.fromFileName(staticFilePath);
            return buildSuccessResponse(contentType, body);
        } catch (UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
        throw new IllegalArgumentException("응답 생성 중에 오류가 발생했습니다.");
    }

    private boolean isRootPath(HttpRequest httpRequest) {
        return httpRequest.getPath() == null
                || httpRequest.getPath().isBlank()
                || httpRequest.getPath().equals("/");
    }

    private String buildSuccessResponse(ContentType contentType, byte[] body) {
        return buildResponse(ResponseStatus.OK, contentType, body);
    }

    private String buildResponse(ResponseStatus responseStatus, ContentType contentType, byte[] body) {
        final var headers = String.join("\r\n",
                HttpVersion.HTTP11.getResponseHeader() + " " + responseStatus.getResponseHeader() + " ",
                contentType.getResponseHeader() + " ",
                "Content-Length: " + body.length + " ",
                "");
        final var bodyString = new String(body, StandardCharsets.UTF_8);
        return headers + "\r\n" + bodyString;
    }

    private String getStaticFilePath(HttpRequest httpRequest) {
        final var staticFilePath = "static" + httpRequest.getPath();
        if (httpRequest.getContentType() == ContentType.HTML && !staticFilePath.endsWith(".html")) {
            return staticFilePath + "." + ContentType.HTML.getExtension();
        }
        return staticFilePath;
    }

    private byte[] readFile(String staticFilePath) {
        try {
            final URL resourceUrl = getClass().getClassLoader().getResource(staticFilePath);
            if (resourceUrl == null) {
                throw new IllegalArgumentException("존재하지 않는 리소스입니다.: " + staticFilePath);
            }
            final var path = resourceUrl.getPath();
            return Files.readString(Path.of(path)).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("파일을 불러오는데 실패했습니다. : {} {}", staticFilePath, e.getMessage(), e);
        }
        throw new IllegalArgumentException("파일을 불러오는데 실패했습니다.: " + staticFilePath);
    }
}
