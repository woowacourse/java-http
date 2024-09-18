package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;

public class HttpResponse {
    //TODO: http상태코드 관리
    private Map<String, String> headers; //TODO: 헤더로 관리
    private String httpStatus;
    private String httpVersion;
    private String location;
    private String mimeType;
    private int contentLength;
    private String body;
    private Cookies cookies = new Cookies();

    public void setRedirect(String httpStatus, String location) {
        this.httpStatus = httpStatus;
        this.location = location;
    }

    public void setResponse(String httpStatus, File file) {
        try {
            this.httpStatus = httpStatus;
            this.mimeType = Files.probeContentType(file.toPath());
            this.body = new String(Files.readAllBytes(file.toPath()));
            this.contentLength = Files.readAllBytes(file.toPath()).length;
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 읽기/쓰기 과정에서 예외 발생 (Path: %s)".formatted(file.toPath()));
        }
    }

    public void setResponse(String httpStatus, String body) {
        this.httpStatus = httpStatus;
        this.mimeType = "text/html";
        this.body = body;
        this.contentLength = body.getBytes().length;
    }

    public void setCookie(Cookie cookie) {
        cookies.setCookie(cookie);
    }

    public String toMessage() {
        StringJoiner message = new StringJoiner("\r\n");
        message.add("HTTP/1.1 %s ".formatted(httpStatus)); // TODO: HTTP 버전은 Request 정보 받아오기
        message.add("Content-Type: %s;charset=utf-8 ".formatted(mimeType));
        message.add("Content-Length: " + contentLength + " ");
        if(location != null) {
            message.add("Location: " + location + " ");
        }
        if(cookies.hasCookie()) {
            message.add("Set-Cookie: " + cookies.toMessage() + " ");
        }
        message.add("");
        message.add(body);

        return message.toString();
    }
}
