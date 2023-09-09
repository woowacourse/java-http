package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
        } catch (IOException | UncheckedServletException e) {
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

    private HttpResponse handleRequest(final HttpRequest request) {
        final Controller controller = RequestMapping.getController(request);
        try {
            return controller.service(request);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}
