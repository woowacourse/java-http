package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.LoginRequestHandler;
import com.techcourse.handler.RegisterRequestHandler;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpCookie;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.request.RequestBody;
import com.techcourse.http.request.RequestHeader;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.http.response.ResponseBody;
import com.techcourse.util.FileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
            String requestLine = bufferedReader.readLine();
            RequestHeader requestHeader = RequestHeader.from(parseRequestHeader(bufferedReader));
            RequestBody requestBody = parseRequestBody(bufferedReader, requestHeader);

            HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);
            HttpResponse httpResponse = handleHttpRequest(httpRequest);

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> parseRequestHeader(
            final BufferedReader reader
    ) throws IOException {
        List<String> httpRequestHeaders = new ArrayList<>();
        String line = reader.readLine();
        if (line == null) {
            return httpRequestHeaders;
        }
        while (!"".equals(line)) {
            httpRequestHeaders.add(line);
            line = reader.readLine();
        }
        return httpRequestHeaders;
    }

    private RequestBody parseRequestBody(
            final BufferedReader reader, final RequestHeader requestHeader
    ) throws IOException {
        if (requestHeader.hasContentLengthKey()) {
            int contentLength = requestHeader.getContentLength();

            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);

            return RequestBody.from(new String(buffer));
        }
        return RequestBody.empty();
    }

    private HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (httpRequest.getFilePath().equals("/login.html")) {
            return loginRequestHandler.handleLoginRequest(httpRequest);
        }
        if (httpRequest.getFilePath().equals("/register.html")) {
            return registerRequestHandler.handleRegisterRequest(httpRequest);
        }
        return createResponse(httpRequest);
    }

    private HttpResponse createResponse(final HttpRequest httpRequest) {
        HttpVersion httpVersion = httpRequest.getHttpVersion();

        if (httpRequest.isRootPath()) {
            return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(), ResponseBody.helloWorld());
        }

        String fileName = FileUtil.createFileName(httpRequest.getFilePath());

        if ("/static/favicon.ico".equals(fileName)) {
            return HttpResponse.noContent(httpVersion, ContentType.IMAGE_X_ICON, HttpCookie.empty(),
                    ResponseBody.empty());
        }

        return HttpResponse.ok(httpVersion, httpRequest.getContentType(), HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
    }
}
