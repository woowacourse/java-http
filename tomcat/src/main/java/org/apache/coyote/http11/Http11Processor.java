package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.TomcatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final TomcatController tomcatController;

    public Http11Processor(final Socket connection, TomcatController tomcatController) {
        this.connection = connection;
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream()) {

            final var requestData = getRequestData(bufferedReader);
            log.info("requestData : {}", requestData);
            tomcatController.handleRequest(requestData);
            final var response = getResponse(requestData);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestData getRequestData(BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .toList();
        if (rawHttpRequest.isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return RequestData.of(rawHttpRequest);
    }

    private String getResponse(RequestData requestData) {
        try {
            if (isRootPath(requestData)) {
                final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                return buildSuccessResponse(HttpContentType.HTML, body);
            }
            final String staticFilePath = getStaticFilePath(requestData);
            final byte[] body = readFile(staticFilePath);
            final var contentType = HttpContentType.fromFileName(staticFilePath);
            return buildSuccessResponse(contentType, body);
        } catch (UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
        throw new IllegalArgumentException("응답 생성 중에 오류가 발생했습니다.");
    }

    private boolean isRootPath(RequestData requestData) {
        return requestData.getResource() == null
                || requestData.getResource().isBlank()
                || requestData.getResource().equals("/");
    }

    private String buildSuccessResponse(HttpContentType contentType, byte[] body) {
        return buildResponse(HttpStatus.OK, contentType, body);
    }

    private String buildResponse(HttpStatus httpStatus, HttpContentType contentType, byte[] body) {
        final var headers = String.join("\r\n",
                HttpVersion.V_11.getResponseHeader() + " " + httpStatus.getResponseHeader() + " ",
                contentType.getResponseHeader() + " ",
                "Content-Length: " + body.length + " ",
                "");
        final var bodyString = new String(body, StandardCharsets.UTF_8);
        return headers + "\r\n" + bodyString;
    }

    private String getStaticFilePath(RequestData requestData) {
        final var staticFilePath = "static" + requestData.getResource();
        if (requestData.getHttpContentType() == HttpContentType.HTML && !staticFilePath.endsWith(".html")) {
            return staticFilePath + "." + HttpContentType.HTML.getExtension();
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
