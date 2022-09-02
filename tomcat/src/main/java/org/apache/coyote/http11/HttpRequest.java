package org.apache.coyote.http11;

import nextstep.jwp.presentation.LoginController;
import org.apache.coyote.util.FileUtils;

public class HttpRequest {

    public static final int METHOD = 0;
    public static final int URI = 1;
    public static final int VERSION = 2;

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    public HttpRequest(final HttpMethod method, final String uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static HttpRequest of(final String startLine) {
        return split(startLine);
    }

    private static HttpRequest split(final String line) {
        String[] split = line.split(" ");
        return new HttpRequest(HttpMethod.valueOf(split[METHOD]), split[URI], HttpVersion.of(split[VERSION]));
    }

    public boolean isGetMethod() {
        return method.isGet();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String request() {
        if (uri.equals("/")) {
            return index();
        }
        if (uri.endsWith(".html")) {
            return html();
        }
        if (uri.endsWith(".css")) {
            return css();
        }
        if (uri.endsWith(".js")) {
            return js();
        }
        if (isQueryString(uri) && isLogin(uri)) {
            QueryStrings queryStrings = new QueryStrings(uri);
            LoginController loginController = new LoginController();
            loginController.login(queryStrings.find("account"), queryStrings.find("password"));
            return "success";
        }

        throw new IllegalArgumentException();
    }

    private String index() {
        String responseBody = "Hello world!";
        return HttpResponse.builder()
                .body(responseBody)
                .version(version)
                .status(HttpStatus.OK.getValue())
                .contentType(ContentType.TEXT_HTML_CHARSET_UTF_8.getValue())
                .contentLength(responseBody.getBytes().length)
                .build()
                .getResponse();
    }

    private String html() {
        String responseBody = FileUtils.readAllBytes(uri);
        return HttpResponse.builder()
                .body(responseBody)
                .version(version)
                .status(HttpStatus.OK.getValue())
                .contentType(ContentType.TEXT_HTML_CHARSET_UTF_8.getValue())
                .contentLength(responseBody.getBytes().length)
                .build()
                .getResponse();
    }

    private String css() {
        String responseBody = FileUtils.readAllBytes(uri);
        return HttpResponse.builder()
                .body(responseBody)
                .version(version)
                .status(HttpStatus.OK.getValue())
                .contentType(ContentType.TEXT_CSS_CHARSET_UTF_8.getValue())
                .contentLength(responseBody.getBytes().length)
                .build()
                .getResponse();
    }

    private String js() {
        String responseBody = FileUtils.readAllBytes(uri);
        return HttpResponse.builder()
                .body(responseBody)
                .version(version)
                .status(HttpStatus.OK.getValue())
                .contentType(ContentType.TEXT_JS_CHARSET_UTF_8.getValue())
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
