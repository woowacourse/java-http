package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
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

    private static RequestBody extractBody(BufferedReader bufferedReader, RequestHeaders requestHeaders) throws IOException {
        if (requestHeaders.requestHasBody()) {
            int contentLength = requestHeaders.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return RequestBody.parse(String.valueOf(buffer));
        }

        return RequestBody.empty();
    }

    public String getBodyParameter(String parameter) {
        return requestBody.getParameter(parameter);
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public boolean hasMethod(Method method) {
        return requestLine.isSameMethod(method);
    }
}
