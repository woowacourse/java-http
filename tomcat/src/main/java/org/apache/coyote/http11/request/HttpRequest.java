package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpHeaders header;
    private final RequestBody requestBody;

    private HttpRequest(StartLine startLine, HttpHeaders header, RequestBody requestBody) {
        this.startLine = startLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader bufferedReader) {
        try{
            final StartLine startLine = StartLine.from(bufferedReader.readLine());
            final List<String> headers = new ArrayList<>();
            String line;
            while(!"".equals(line = bufferedReader.readLine())){
                headers.add(line);
            }
            final HttpHeaders httpHeaders = HttpHeaders.from(headers);
            final int contentLength = Integer.parseInt(httpHeaders.getHeaderValue("Content-Length").trim());
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            final String requestBody = new String(buffer);
            return new HttpRequest(startLine, httpHeaders, RequestBody.from(requestBody));
        } catch (IOException exception) {
            throw new UncheckedServletException("유효하지 않은 요청입니다.");
        }
    }

    public Path getPath() {
        return startLine.getPath();
    }

    public Map<String, String> getHeader() {
        return header.getValues();
    }

    public Map<String, String> getBody() {
        return requestBody.getValues();
    }

    public boolean isGetMethod() {
        return startLine.isGet();
    }

    public boolean isPostMethod() {
        return startLine.isPost();
    }

    public boolean isStaticResource() {
        return getPath().isStaticResource();
    }
}
