package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static HttpRequest parseFromSocket(Socket socket) {
        try {
            final var inputStream = socket.getInputStream();
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
            final String httpRequestMethod = stringTokenizer.nextToken();
            final String httpRequestUri = stringTokenizer.nextToken();

            String headerLine;
            while ((headerLine = bufferedReader.readLine()).length() != 0) {
                System.out.println(headerLine);
            }

            final StringBuilder requestBody = getRequestBody(bufferedReader);

            return new HttpRequest(httpRequestUri, httpRequestMethod, requestBody.toString());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static StringBuilder getRequestBody(BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestBody = new StringBuilder();
        while (bufferedReader.ready()) {
            requestBody.append((char) bufferedReader.read());
        }
        System.out.println(requestBody);
        return requestBody;
    }
}
