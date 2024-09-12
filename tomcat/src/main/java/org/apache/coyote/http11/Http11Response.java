package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.Http11HeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.Http11HeaderName.CONTENT_TYPE;
import static org.apache.coyote.http11.Http11HeaderName.LOCATION;
import static org.apache.coyote.http11.Http11HeaderName.SET_COOKIE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final String ROOT_PATH = "/";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String RESPONSE_FORMAT = String.join("\r\n", "%s", "%s", "%s");
    private static final String JSESSIONID = "JSESSIONID=";
    private static final String HELLO_WORLD = "Hello world!";
    private static final String HTML_401 = "/401.html";
    private static final String HTML_404 = "/404.html";
    private static final String HTML_500 = "/500.html";
    private static final String HTML_INDEX = "/index.html";
    private static final Logger log = LoggerFactory.getLogger(Http11Response.class);

    private StatusLine statusLine;
    private Http11ResponseHeader responseHeader;
    private Http11ResponseBody responseBody;

    public Http11Response(StatusLine statusLine,
                          Http11ResponseHeader responseHeader,
                          Http11ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static Http11Response of(Http11Request request) {
        RequestLine requestLine = request.getRequestLine();
        HttpVersion httpVersion = requestLine.getHttpVersion();

        return new Http11Response(StatusLine.of(httpVersion, 200),
                Http11ResponseHeader.of(),
                Http11ResponseBody.of());
    }

    public void unAuthorized(Http11Request request) {
        setStaticResponse(request, HTML_401, 401);
    }

    public void notFound(Http11Request request) {
        setStaticResponse(request, HTML_404, 404);
    }

    public void internalServerError(Http11Request request) {
        setStaticResponse(request, HTML_500, 500);
    }

    public void loginSuccess(String uuid) {
        setRedirectResponse(HTML_INDEX, 302, List.of(JSESSIONID + uuid));
    }

    public void setRedirectResponse(String resourcePath, int statusCode, List<String> cookies) {
        String responseBody = getStaticResource(resourcePath);
        this.statusLine = StatusLine.of(this.statusLine.getHttpVersion(), statusCode);
        this.responseHeader = Http11ResponseHeader.builder()
                .addHeader(CONTENT_TYPE.getName(), List.of(HTML.getContentType()))
                .addHeader(CONTENT_LENGTH.getName(), List.of(String.valueOf(responseBody.length())))
                .addHeader(LOCATION.getName(), List.of(resourcePath))
                .addHeader(SET_COOKIE.getName(), cookies)
                .build();
        this.responseBody = Http11ResponseBody.of(responseBody);
    }

    public void setStaticResponse(Http11Request request, String resourcePath, int statusCode) {
        Http11RequestHeader http11RequestHeader = request.getHttp11RequestHeader();
        ContentType contentType = ContentType.from(http11RequestHeader.getAcceptType());
        String responseBody = getStaticResource(resourcePath);
        this.statusLine = StatusLine.of(this.statusLine.getHttpVersion(), statusCode);
        this.responseHeader = Http11ResponseHeader.builder()
                .addHeader(CONTENT_TYPE.getName(), List.of(contentType.getContentType()))
                .addHeader(CONTENT_LENGTH.getName(), List.of(String.valueOf(responseBody.length())))
                .build();
        this.responseBody = Http11ResponseBody.of(responseBody);
    }


    private String getStaticResource(String resourcePath) {
        if (ROOT_PATH.equals(resourcePath)) {
            return HELLO_WORLD;
        }
        return readResource(STATIC_RESOURCE_PATH + resourcePath).orElseGet(this::readNotFoundPage);
    }

    private String readNotFoundPage() {
        return readResource(STATIC_RESOURCE_PATH + HTML_404).get();
    }

    private Optional<String> readResource(String resourcePath) {
        try (InputStream inputStream = Http11RequestHandler.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                return Optional.of(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public String getResponse() {
        return String.format(RESPONSE_FORMAT,
                statusLine.getStatusLine(),
                responseHeader.getAllHeaders(),
                responseBody.getBody());
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Http11ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public Http11ResponseBody getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return "Http11Response{" +
                "statusLine=" + statusLine +
                ", responseHeader=" + responseHeader +
                ", responseBody=" + responseBody +
                '}';
    }
}
