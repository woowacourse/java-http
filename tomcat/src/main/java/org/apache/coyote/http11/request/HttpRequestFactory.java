package org.apache.coyote.http11.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HttpRequestFactory {

    private static final String BLANK = "";
    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String CONTENT_LENGTH = "Content-Length";
    private static final Logger log = LoggerFactory.getLogger(HttpRequestFactory.class);

    public static HttpRequest readFrom(final Socket socket) {
        try (final var inputStream = socket.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final String firstLine = bufferedReader.readLine();

            final RequestLine requestLine = RequestLine.from(firstLine);
            final RequestHeaders header = getRequestHeaders(bufferedReader);
            final RequestBody body = getRequestBody(bufferedReader, header);

            return new HttpRequest(requestLine, header, body);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static RequestHeaders getRequestHeaders(final BufferedReader br) throws IOException {
        final RequestHeaders headers = new RequestHeaders();
        String line = br.readLine();
        while (!BLANK.equals(line)) {
            String[] header = line.split(HEADER_DELIMITER);
            headers.set(header[KEY_INDEX], header[VALUE_INDEX]);
            line = br.readLine();
        }
        return headers;
    }

    private static RequestBody getRequestBody(final BufferedReader br, final RequestHeaders headers) throws IOException {
        if (!headers.contains(CONTENT_LENGTH)) {
            return null;
        }
        final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }
}
