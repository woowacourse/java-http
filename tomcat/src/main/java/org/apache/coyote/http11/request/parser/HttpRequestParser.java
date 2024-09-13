package org.apache.coyote.http11.request.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.RequestBody;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        HttpRequestLine requestLine = HttpRequestLine.from(bufferedReader.readLine());
        List<String> headers = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            headers.add(line);
            line = bufferedReader.readLine();
        }
        HttpRequestHeader requestHeader = HttpRequestHeader.from(headers);
        String contentLengthHeader = requestHeader.getValue(HttpHeaders.CONTENT_LENGTH);
        String contentTypeValue = requestHeader.getValue(HttpHeaders.CONTENT_TYPE);
        String enCodedBody = "";
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            enCodedBody = URLDecoder.decode(new String(body), StandardCharsets.UTF_8);
        }
        RequestBody body = RequestBody.from(enCodedBody, contentTypeValue);
        return new HttpRequest(requestLine, requestHeader, body);
    }
}
