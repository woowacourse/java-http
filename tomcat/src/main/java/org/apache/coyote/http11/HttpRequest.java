package org.apache.coyote.http11;

import nextstep.jwp.presentation.LoginController;
import org.apache.coyote.util.FileUtils;

public class HttpRequest {

    public static final String INDEX_BODY = "Hello world!";
    private final HttpMethod method;
    private final HttpRequestUri uri;
    private final HttpVersion version;

    public HttpRequest(final HttpMethod method, final HttpRequestUri uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public HttpRequest(final String method, final String uri, final String version) {
        this(HttpMethod.of(method), HttpRequestUri.of(uri), HttpVersion.of(version));
    }

    public boolean isGetMethod() {
        return method.isGet();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequestUri getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String request() {
        if (uri.isIndex()) {
            return response(INDEX_BODY);
        }

        if (isQueryString(uri.getValue()) && isLogin(uri.getValue())) {
            QueryStrings queryStrings = new QueryStrings(uri.getValue());
            LoginController loginController = new LoginController();
            loginController.login(queryStrings.find("account"), queryStrings.find("password"));
            return "success";
        }
        return response();
    }

    private String response() {
        String responseBody = FileUtils.readAllBytes(uri.getValue());
        return HttpResponse.builder()
                .body(responseBody)
                .version(version)
                .status(HttpStatus.OK.getValue())
                .contentType(uri.getContentType().getValue())
                .contentLength(responseBody.getBytes().length)
                .build()
                .getResponse();
    }

    private String response(final String responseBody) {
        return HttpResponse.builder()
                .body(responseBody)
                .version(version)
                .status(HttpStatus.OK.getValue())
                .contentType(uri.getContentType().getValue())
                .contentLength(responseBody.getBytes().length)
                .build()
                .getResponse();
    }

    private boolean isLogin(final String uri) {
        String requestUri = uri.substring(0, uri.indexOf("?"));
        return requestUri.equals("/login");
    }

    private boolean isQueryString(final String uri) {
        return uri.contains("?");
    }
}
