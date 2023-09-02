package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.common.Headers;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.ResponseBody;
import org.apache.coyote.exception.CoyoteHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HttpHeader.ACCEPT;
import static org.apache.coyote.common.HttpHeader.CONNECTION;
import static org.apache.coyote.common.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.common.HttpHeader.HOST;
import static org.apache.coyote.common.MediaType.TEXT_CSS;
import static org.apache.coyote.common.MediaType.TEXT_HTML;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));) {

            final HttpRequest request = HttpRequest.from(br);
            log.info("=============> HTTP Request : \n {}", request);
            final URI requestUri = request.requestUri();
            final Headers requestHeaders = request.headers();

            final ResponseBody responseBody = parseToResponseBody(requestUri);
            final Headers responseHeaders = parseToResponseHeaders(requestHeaders, responseBody);

            final HttpResponse response = new HttpResponse(
                    request.httpVersion(),
                    HttpStatus.OK,
                    responseHeaders,
                    responseBody
            );
            log.info("=============> HTTP Response : \n {}", response);

            outputStream.write(response.bytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Headers parseToResponseHeaders(final Headers httpRequestHeaders, final ResponseBody responseBody) {
        final Map<String, String> responseHeaderMapping = new HashMap<>();

        final String headerAcceptValue = httpRequestHeaders.getHeaderValue(ACCEPT.source());
        if (Objects.isNull(headerAcceptValue)) {
            responseHeaderMapping.put(CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source());
        }
        if (Objects.nonNull(headerAcceptValue)) {
            if (headerAcceptValue.contains(TEXT_HTML.source())) {
                responseHeaderMapping.put(CONTENT_TYPE.source(), TEXT_HTML.source() + ";charset=utf-8");
            }
            if (headerAcceptValue.contains(TEXT_CSS.source())) {
                responseHeaderMapping.put(CONTENT_TYPE.source(), TEXT_CSS.source());
            }
        }
        responseHeaderMapping.put(CONTENT_LENGTH.source(), String.valueOf(responseBody.length()));

        httpRequestHeaders.headerNames()
                .stream()
                .filter(headerName -> !headerName.equals(ACCEPT.source()))
                .filter(headerName -> !headerName.equals(CONNECTION.source()))
                .filter(headerName -> !headerName.equals(HOST.source()))
                .forEach(headerName -> responseHeaderMapping.put(headerName, httpRequestHeaders.getHeaderValue(headerName)));

        final Headers responseHeaders = new Headers(responseHeaderMapping);
        return responseHeaders;
    }

    private ResponseBody parseToResponseBody(final URI httpRequestUri) throws IOException {
        try {
            ResponseBody responseBody = new ResponseBody("Hello world!");
            String requestUri = httpRequestUri.getPath();
            if (!requestUri.equals("/")) {
                if (requestUri.endsWith(".html") || requestUri.endsWith(".css") || requestUri.endsWith(".js") || requestUri.endsWith("ico")) {

                } else {
                    requestUri += ".html";
                }
                final URL resourceURL = getClass().getClassLoader().getResource("static" + requestUri);
                final Path resourcePath = new File(resourceURL.getFile()).toPath();
                responseBody = new ResponseBody(Files.readString(resourcePath));
            }

            return responseBody;
        } catch (NullPointerException | IOException e) {
            log.warn("[ERROR] RequestURI = {}", httpRequestUri);
            log.warn("[ERROR] ErrorMessage = {} ", e.getMessage(), e);
            throw new CoyoteHttpException("응답 바디를 파싱하던 도중에 예외가 발생하였습니다.");
        }
    }
}
