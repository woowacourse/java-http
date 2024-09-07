package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.exception.UncheckedHttpException;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        HttpRequestLine requestLine = new HttpRequestLine(bufferedReader.readLine());
        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            String[] splitLine = line.split(": ");
            if (splitLine.length != 2) {
                throw new UncheckedHttpException(new IllegalArgumentException());
            }
            headers.put(splitLine[0], splitLine[1]);
            line = bufferedReader.readLine();
        }

        Map<String, String> bodies = Collections.emptyMap();
        String contentLengthHeader = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            String requestBody = URLDecoder.decode(new String(body), StandardCharsets.UTF_8);
            String[] splitBody = requestBody.split("&");
            bodies = Arrays.stream(splitBody)
                    .map(str -> str.split("="))
                    .collect(Collectors.toUnmodifiableMap(split -> split[0], split -> split[1]));
        }
        return new HttpRequest(requestLine, headers, bodies);
    }
}
