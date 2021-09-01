package nextstep.jwp.http;

import java.util.Objects;
import nextstep.jwp.http.message.HttpCookie;
import nextstep.jwp.http.message.HttpCookies;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpStatus;

public class HttpResponseImpl implements HttpResponse {
    private static final String DEFAULT_VERSION_OF_PROTOCOL = "HTTP/1.1";
    private static final String SET_COOKIE = "Set-Cookie";

    private String content;
    private HttpHeaders headers;
    private HttpCookies cookies;
    private HttpStatus status;
    private String versionOfProtocol;

    public HttpResponseImpl() {
        headers = new HttpHeaders();
        cookies = new HttpCookies();
    }

    @Override
    public String getContentAsString() {
        return content;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getContent() {
        if (Objects.isNull(content)) {
            return "";
        }
        return content;
    }

    @Override
    public String getStatusAsString() {
        return String.valueOf(status.getStatus());
    }

    @Override
    public String getVersionOfProtocol() {
        if (Objects.isNull(versionOfProtocol)) {
            return DEFAULT_VERSION_OF_PROTOCOL;
        }
        return versionOfProtocol;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public void setVersionOfProtocol(String versionOfProtocol) {
        this.versionOfProtocol = versionOfProtocol;
    }

    @Override
    public void addCookie(HttpCookie cookie) {
        this.cookies.addCookie(cookie);
        this.headers.addHeader(SET_COOKIE, cookies.asString());
    }

    @Override
    public HttpCookies getCookies() {
        return cookies;
    }

    @Override
    public String asString() {
        return String.join("\r\n",
            getVersionOfProtocol() + " " + status.getStatus() + " " + status.getDescription() + " ",
            headers.asString(),
            getContent()
            );
    }
}
