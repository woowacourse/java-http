package org.apache.coyote.http11;

import com.techcourse.exception.NotFoundException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.LoginRequestHandler;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final LoginRequestHandler loginRequestHandler = new LoginRequestHandler(HttpVersion.HTTP_1_1);
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
        try (final InputStream inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = connection.getOutputStream()
        ) {
            String line = bufferedReader.readLine();
            HttpRequest httpRequest = HttpRequest.from(line);

            HttpResponse response = handleHttpRequest(httpRequest);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (httpRequest.getFilePath().equals("/login.html")) {
            return loginRequestHandler.handleLoginRequest(httpRequest);
        }
        return createResponseBody(httpRequest);
    }

    private HttpResponse createResponseBody(final HttpRequest httpRequest) {
        if (httpRequest.isRootPath()) {
            return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, ContentType.TEXT_HTML, "Hello world!");
        }

        String fileName = createFileName(httpRequest.getFilePath());
        if ("/static/favicon.ico".equals(fileName)) {
            return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.NO_CONTENT, ContentType.IMAGE_X_ICON, "");
        }

        String responseBody = readResource(fileName);
        return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, httpRequest.getContentType(), responseBody);
    }

    private String readResource(final String fileName) {
        try {
            URL url = getClass().getResource(fileName);
            Objects.requireNonNull(url, fileName + "에 파일이 없습니다.");

            Path filePath = Paths.get(url.toURI());
            return Files.readString(filePath);
        } catch (URISyntaxException | IOException | NullPointerException e) {
            throw new NotFoundException("존재하지 않는 파일입니다. :" + e.getMessage());
        }
    }

    private String createFileName(String path) {
        return String.format("/static/%s", toNormalizedPath(path));
    }

    private String toNormalizedPath(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }
}
