package org.apache.coyote.http11.request;


import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.coyote.http11.request.element.HttpRequestBody;
import org.apache.coyote.http11.request.element.HttpRequestHeader;
import org.apache.coyote.http11.request.element.Path;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HttpRequest {

    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(HttpRequestHeader header, HttpRequestBody body) {
        this.header = header;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return header.getMethod();
    }

    public Path getPath() {
        return header.getPath();
    }

    public String getBody() {
        return body.getBodyContext();
    }

    public Session getSession(boolean created) {
        if (created) {
            return new Session(header.find("Cookie").split("JSESSIONID=")[1]);
        }
        return new Session(String.valueOf(UUID.randomUUID()));
    }

    public boolean isCookieExist() {
        return header.find("Cookie") != null;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
