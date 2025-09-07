package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.LoginRequestHandler;
import com.techcourse.handler.RegisterRequestHandler;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.common.Location;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.util.FileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final LoginRequestHandler loginRequestHandler = new LoginRequestHandler(HttpVersion.HTTP_1_1);
    private final RegisterRequestHandler registerRequestHandler = new RegisterRequestHandler(HttpVersion.HTTP_1_1);
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
        if (httpRequest.getFilePath().equals("/login.html") && httpRequest.getRequestParams().containsKey("account")) {
            return loginRequestHandler.handleLoginRequest(httpRequest);
        }
        if (httpRequest.getFilePath().equals("/register.html")) {
            return registerRequestHandler.handleRegisterRequest(httpRequest);
        }
        return createResponseBody(httpRequest);
    }

    private HttpResponse createResponseBody(final HttpRequest httpRequest) {
        HttpVersion httpVersion = httpRequest.getHttpVersion();

        if (httpRequest.isRootPath()) {
            return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(),
                    ContentType.TEXT_HTML, "Hello world!");
        }

        String fileName = FileUtil.createFileName(httpRequest.getFilePath());

        if ("/static/favicon.ico".equals(fileName)) {
            return new HttpResponse(httpVersion, HttpStatus.NO_CONTENT, Location.empty(),
                    ContentType.IMAGE_X_ICON, "");
        }

        String responseBody = FileUtil.readResource(fileName);
        return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(),
                httpRequest.getContentType(), responseBody);
    }
}
