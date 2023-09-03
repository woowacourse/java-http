package org.apache.coyote.http11;

import java.util.Arrays;

import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.ContentType.ICO;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

public enum RequestMapper {
    HOME("/", "/", GET, OK, HTML, false, false),
    INDEX("/index.html", "/index.html", GET, OK, HTML, false, false),
    SCRIPTS_JS("/js/scripts.js", "/js/scripts.js", GET, OK, HTML, false, false),
    CHART_AREA_JS("/assets/chart-area.js", "/assets/chart-area.js", GET, OK, HTML, false, false),
    CHART_BAR_JS("/assets/chart-bar.js", "/assets/chart-bar.js", GET, OK, HTML, false, false),
    CHART_PIE_JS("/assets/chart-pie.js", "/assets/chart-pie.js", GET, OK, HTML, false, false),
    FAVICON("/favicon.ico", "/favicon.ico", GET, OK, ICO, false, false),
    CSS("/css/styles.css", "/css/styles.css", GET, OK, ContentType.CSS, false, false),
    LOG_IN("/login.html", "/login.html", GET, FOUND, HTML, false, false),
    LOG_IN_WITH_INFOS("/login", null, GET, FOUND, HTML, true, true),
    UNAUTHORIZED("/401.html", "/401.html", GET, OK, HTML, false, false),
    ;

    private final String uri;
    private final String path;
    private final HttpMethod httpMethod;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final boolean useQueryString;
    private final boolean redirect;

    RequestMapper(String uri, String path, HttpMethod httpMethod, HttpStatus httpStatus, ContentType contentType, boolean useQueryString, boolean redirect) {
        this.uri = uri;
        this.path = path;
        this.httpMethod = httpMethod;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.useQueryString = useQueryString;
        this.redirect = redirect;
    }

    public static RequestMapper findMapper(HttpRequestFirstLineInfo httpRequestFirstLineInfo) {
        boolean useQueryString = httpRequestFirstLineInfo.getQueryStringParser() != null;
        return Arrays.stream(RequestMapper.values())
                .filter(requestMapper -> httpRequestFirstLineInfo.getHttpMethod().equals(requestMapper.httpMethod))
                .filter(requestMapper -> httpRequestFirstLineInfo.getUri().equals(requestMapper.uri))
                .filter(requestMapper -> requestMapper.isUseQueryString() == useQueryString)
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

    public boolean isUseQueryString() {
        return useQueryString;
    }

    public boolean isRedirect() {
        return redirect;
    }
}
