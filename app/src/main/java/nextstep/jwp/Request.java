package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Request {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request createFromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        RequestLine requestLine = RequestLine.createFromBufferedReader(bufferedReader);
        RequestHeader requestHeader = RequestHeader.createFromBufferedReader(bufferedReader);
        RequestBody requestBody = RequestBody.createFromBufferedReader(bufferedReader, requestHeader.getBodySize());

        return new Request(requestLine, requestHeader, requestBody);
    }

    public String getRequestLine(String requestLineName) {
        return this.requestLine.get(requestLineName);
    }

    public boolean isEmpty() {
        return requestLine.isEmpty();
    }

    public String getBody() {
        return requestBody.getRequestBody();
    }
}
