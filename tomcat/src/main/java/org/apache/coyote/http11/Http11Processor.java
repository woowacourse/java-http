package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = parseHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }
            final HttpResponse response = handleRequest(httpRequest);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestStartLine startLine = extractStartLine(bufferedReader);
        if (startLine == null) {
            return null;
        }
        final HttpRequestHeader httpRequestHeaders = extractHeader(bufferedReader);
        final HttpRequestBody httpRequestBody = extractBody(httpRequestHeaders.getContentLength(), bufferedReader);
        return new HttpRequest(startLine, httpRequestHeaders, httpRequestBody);
    }

    private HttpRequestStartLine extractStartLine(final BufferedReader bufferedReader) throws IOException {
        return HttpRequestStartLine.from(bufferedReader.readLine());
    }

    private HttpRequestHeader extractHeader(final BufferedReader bufferedReader)
            throws IOException {
        Map<String, String> parsedHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] headerTokens = line.split(": ");
            parsedHeaders.put(headerTokens[0], headerTokens[1]);
            line = bufferedReader.readLine();
        }
        return new HttpRequestHeader(parsedHeaders);
    }

    private HttpRequestBody extractBody(String contentLength, BufferedReader bufferedReader)
            throws IOException {
        if (contentLength == null) {
            return null;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new HttpRequestBody(new String(buffer));
    }

    private HttpResponse handleRequest(final HttpRequest request)
            throws URISyntaxException, IOException {
        final Controller controller = RequestMapping.getController(request);
        try {
            return controller.service(request);
        } catch (Exception exception) { // TODO: 2023/09/08  커스텀 에외 만들기
            return handle500();
        }
    }

    private HttpResponse handle500() throws IOException, URISyntaxException {
        String responseBody = getHtmlFile(getClass().getResource("/static/500.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.INTERNAL_SERVER_ERROR, responseHeader, responseBody);
    }

    // TODO: 2023/09/08 util로 분리
    private String getHtmlFile(URL filePathUrl) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(Objects.requireNonNull(filePathUrl).toURI());
        return new String(Files.readAllBytes(filePath));
    }
}
