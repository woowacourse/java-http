package org.apache.coyote.http11;

import java.util.Arrays;

import static org.apache.coyote.http11.ContentType.*;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

public enum RequestMapper {
    HOME("/", "/", GET, OK, HTML),
    INDEX("/index.html", "/index.html", GET, OK, HTML),
    SCRIPTS_JS("/js/scripts.js", "/js/scripts.js", GET, OK, JS),
    CHART_AREA_JS("/assets/chart-area.js", "/assets/chart-area.js", GET, OK, JS),
    CHART_BAR_JS("/assets/chart-bar.js", "/assets/chart-bar.js", GET, OK, JS),
    CHART_PIE_JS("/assets/chart-pie.js", "/assets/chart-pie.js", GET, OK, JS),
    FAVICON("/favicon.ico", "/favicon.ico", GET, OK, ICO),
    CSS("/css/styles.css", "/css/styles.css", GET, OK, ContentType.CSS),
    LOG_IN("/login", "/login.html", GET, FOUND, HTML),
    LOG_IN_WITH_INFOS("/login", null, POST, FOUND, HTML),
    UNAUTHORIZED("/401.html", "/401.html", GET, OK, HTML),
    REGISTER("/register", "/register.html", GET, OK, HTML),
    REGISTER_WITH_INFOS("/register", null, POST, FOUND, HTML),
    ;

    private final String uri;
    private final String path;
    private final HttpMethod httpMethod;
    private final HttpStatus httpStatus;
    private final ContentType contentType;

    RequestMapper(String uri, String path, HttpMethod httpMethod, HttpStatus httpStatus, ContentType contentType) {
        this.uri = uri;
        this.path = path;
        this.httpMethod = httpMethod;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
    }

    public static RequestMapper findMapper(HttpRequestFirstLineInfo httpRequestFirstLineInfo) {
        return Arrays.stream(RequestMapper.values())
                .filter(requestMapper -> httpRequestFirstLineInfo.getHttpMethod().equals(requestMapper.httpMethod))
                .filter(requestMapper -> httpRequestFirstLineInfo.getUri().equals(requestMapper.uri))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
    }

    public String getPath() {
        return path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
