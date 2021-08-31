package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String EMPTY_STRING = "";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders,
                        RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        RequestHeaders requestHeaders = RequestHeaders.parse(bufferedReader);
        RequestBody requestBody = extractBody(bufferedReader, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private static RequestBody extractBody(BufferedReader bufferedReader,
                                           RequestHeaders requestHeaders) throws IOException {
        if (requestHeaders.requestHasBody()) {
            int contentLength = requestHeaders.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return RequestBody.parse(String.valueOf(buffer));
        }

        return RequestBody.empty();
    }

    public boolean hasMethod(Method method) {
        return requestLine.isSameMethod(method);
    }

    public boolean hasQueryParam() {
        return requestLine.hasQueryParam();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getUriParameter(String parameter) {
        return requestLine.getUriParameter(parameter);
    }

    public String getBodyParameter(String parameter) {
        return requestBody.getParameter(parameter);
    }

    @Override
    public String toString() {
        if (requestHeaders.requestHasBody()) {
            return String.join(NEW_LINE,
                requestLine.toString(),
                requestHeaders.toString(),
                EMPTY_STRING,
                requestBody.toString()
            );
        }

        return String.join(NEW_LINE,
            requestLine.toString(),
            requestHeaders.toString()
        );
    }
}
