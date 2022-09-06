package org.apache.coyote.http11;

import static org.apache.coyote.request.HttpMethod.POST;
import static org.apache.coyote.response.StatusCode.OK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ": ";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = readHttpRequest(bufferedReader);
            final HttpMethod requestMethod = httpRequest.getRequestMethod();
            String requestUrl = httpRequest.getRequestUrlWithoutQuery();
            HttpResponse httpResponse = HttpResponse.of(OK, ContentType.from(requestUrl), requestUrl);

            if (requestUrl.contains("login") && requestMethod.equals(POST)) {
                httpResponse = LoginHandler.login(httpRequest.getRequestBody(), httpRequest.getCookies());
            }
            if (requestUrl.contains("register") && requestMethod.equals(POST)) {
                httpResponse = RegisterHandler.register(httpRequest.getRequestBody());
            }

            final String response = httpResponse.getResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String httpStartLine = bufferedReader.readLine();
        final Map<String, String> httpHeaderLines = readHttpHeaderLines(bufferedReader);
        final String requestBody = readRequestBody(bufferedReader, httpHeaderLines);

        return HttpRequest.of(httpStartLine, httpHeaderLines, requestBody);
    }

    private static Map<String, String> readHttpHeaderLines(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> httpHeaderLines = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }
            final String[] header = line.split(HEADER_DELIMITER);
            httpHeaderLines.put(header[KEY_INDEX], header[VALUE_INDEX].trim());
        }

        return httpHeaderLines;
    }

    private static String readRequestBody(BufferedReader bufferedReader, Map<String, String> httpHeaderLines)
            throws IOException {
        final String contentLengthHeader = httpHeaderLines.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader.trim());
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }
}
