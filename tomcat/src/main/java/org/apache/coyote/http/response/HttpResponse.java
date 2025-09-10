package org.apache.coyote.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.exception.RequestProcessingException;
import org.apache.coyote.exception.SocketWriteException;
import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.value.HttpHeader;
import org.apache.coyote.http.value.HttpVersion;
import org.apache.coyote.http.value.StatusCode;

public class HttpResponse {

    private HttpVersion httpVersion;
    private StatusCode statusCode;
    private Map<String, String> headers = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private String body;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void writeMessage(OutputStream outputStream) {
        try {
            String message = body == null ? makeMassageWhenEmptyBody() : makeMassageWhenBody();
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new SocketWriteException("소켓에 데이터를 쓰는중 오류가 발생했습니다.");
        }
    }

    public boolean isProcessed() {
        return statusCode != null && !headers.isEmpty();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    public void setCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setRedirection(String location) {
        setStatusCode(StatusCode.FOUND);
        setHeader(HttpHeader.LOCATION.getValue(), location);
    }

    private String makeMassageWhenBody() {
        validateCanMakeMessage();
        List<String> messageLines = new ArrayList<>();
        messageLines.add(makeStartLine());
        messageLines.addAll(makeCustomHeaderLine());
        messageLines.addAll(makeSetCookieHeaderLine());
        messageLines.add(makeContentLengthHeaderLine());
        return String.join("\r\n", messageLines) + "\r\n" + "\r\n" + body;
    }

    private String makeMassageWhenEmptyBody() {
        validateCanMakeMessage();
        List<String> messageLines = new ArrayList<>();
        messageLines.add(makeStartLine());
        messageLines.addAll(makeCustomHeaderLine());
        messageLines.addAll(makeSetCookieHeaderLine());
        return String.join("\r\n", messageLines) + "\r\n" + "\r\n";
    }


    private String makeStartLine() {
        return String.format("%s %s %s",
                httpVersion.getValue(),
                statusCode.getCode(),
                statusCode.getMessage());
    }

    private List<String> makeCustomHeaderLine() {
        List<String> headerLines = new ArrayList<>();
        List<String> customHeaderKeys = headers.keySet().stream().toList();
        for (String key : customHeaderKeys) {
            String value = headers.get(key);
            headerLines.add(key + ": " + value);
        }
        return headerLines;
    }

    private List<String> makeSetCookieHeaderLine() {
        List<String> cookieLines = new ArrayList<>();
        if (cookies.isEmpty()) {
            return cookieLines;
        }

        for (Cookie cookie : cookies) {
            cookieLines.add(HttpHeader.SET_COOKIE.getValue() + ": " + cookie.makeCookieLine());
        }
        return cookieLines;
    }

    private String makeContentLengthHeaderLine() {
        return String.format("%s: %s",
                HttpHeader.CONTENT_LENGTH.getValue(),
                body.getBytes(StandardCharsets.UTF_8).length);
    }

    private void validateCanMakeMessage() {
        if (httpVersion == null || statusCode == null) {
            throw new RequestProcessingException("주요 응답 필드가 비어있어 응답 메세지를 생성할 수 없습니다.");
        }
    }
}
