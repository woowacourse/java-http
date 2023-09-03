package org.apache.coyote.http11.request;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;

public class Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestParameters requestParameters;
    private final String body;

    private Request(final RequestLine requestLine,
                    final Headers headers,
                    final String body,
                    final RequestParameters requestParameters) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.requestParameters = requestParameters;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaderLines = new ArrayList<>();
        String nextLine;
        while (!"".equals(nextLine = bufferedReader.readLine())) {
            if (nextLine == null) {
                throw new RuntimeException("헤더가 잘못되었습니다.");
            }
            requestHeaderLines.add(nextLine);
        }

        final String requestFirstLine = requestHeaderLines.get(0);
        final RequestLine requestLine = RequestLine.from(requestFirstLine);
        final Headers headers = new Headers();
        headers.addRequestHeaders(requestHeaderLines);
        final RequestParameters requestParameters = RequestParameters.from(requestFirstLine);

        final String contentLengthValue = headers.getValue(CONTENT_LENGTH) ;
        final int contentLength = "".equals(contentLengthValue) ? 0 : Integer.parseInt(contentLengthValue);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String body = new String(buffer);

        return new Request(requestLine, headers, body, requestParameters);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", requestParameters=" + requestParameters +
                ", body='" + body + '\'' +
                '}';
    }
}
