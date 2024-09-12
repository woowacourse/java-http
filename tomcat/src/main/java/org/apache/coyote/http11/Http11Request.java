package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Http11Request {

    private RequestLine requestLine;
    private Http11RequestHeader http11RequestHeader;
    private Http11RequestBody http11RequestBody;

    public Http11Request(RequestLine requestLine,
                         Http11RequestHeader http11RequestHeader,
                         Http11RequestBody http11RequestBody) {
        this.requestLine = requestLine;
        this.http11RequestHeader = http11RequestHeader;
        this.http11RequestBody = http11RequestBody;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        Http11RequestHeader requestHeader = Http11RequestHeader.from(bufferedReader);
        int contentLength = requestHeader.getContentLength();
        Http11RequestBody requestBody = Http11RequestBody.of(bufferedReader, contentLength);

        return new Http11Request(requestLine, requestHeader, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Http11RequestHeader getHttp11RequestHeader() {
        return http11RequestHeader;
    }

    public Http11RequestBody getHttp11RequestBody() {
        return http11RequestBody;
    }

    @Override
    public String toString() {
        return "Http11Request{" +
                "requestLine=" + requestLine +
                ", http11RequestHeader=" + http11RequestHeader +
                ", http11RequestBody=" + http11RequestBody +
                '}';
    }
}
