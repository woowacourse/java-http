package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.MediaType;
import org.apache.coyote.http11.request.bodyparser.PlainTextParser;
import org.apache.coyote.http11.request.bodyparser.WebFormUrlParser;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        HttpRequestLine requestLine = HttpRequestLine.from(bufferedReader.readLine());
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
        String contentLengthHeader = headers.get(HttpHeaders.CONTENT_LENGTH);
        String contentTypeValue = headers.get(HttpHeaders.CONTENT_TYPE);
        String enCodedBody = "";
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            enCodedBody = URLDecoder.decode(new String(body), StandardCharsets.UTF_8);
        }
        return new HttpRequest(requestLine, headers, getRequestBody(enCodedBody, contentTypeValue));
    }

    private static RequestBody getRequestBody(String body, String mediaTypeValue) {
        if (StringUtils.isBlank(mediaTypeValue)) {
            return new RequestBody();
        }
        MediaType mediaType = MediaType.from(mediaTypeValue);

        if (MediaType.APPLICATION_FORM_URLENCODED.equals(mediaType)) {
            return new RequestBody(body, new WebFormUrlParser());
        }
        if (MediaType.TEXT_PLAIN.equals(mediaType)) {
            return new RequestBody(body, new PlainTextParser());
        }
        return new RequestBody();
    }
}
