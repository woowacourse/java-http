package org.apache.support;

import java.net.URI;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.tomcat.http.common.Method;
import org.apache.tomcat.http.common.Version;
import org.apache.tomcat.http.common.body.Body;
import org.apache.tomcat.http.common.body.TextTypeBody;
import org.apache.tomcat.http.request.RequestHeaders;
import org.apache.tomcat.http.request.RequestLine;
import org.apache.tomcat.http.response.Cookie;
import org.apache.tomcat.http.response.ResponseHeader;
import org.apache.tomcat.http.response.StatusCode;
import org.apache.tomcat.http.response.StatusLine;

public class Dummy {

    public static HttpRequest getHttpRequest() {
        final var requestLine = new RequestLine(Method.GET, URI.create("/"), new Version(1, 1));
        final var requestHeaders = new RequestHeaders("");
        final var textTypeBody = new TextTypeBody("");
        final var cookie = new Cookie("");
        return new HttpRequest(requestLine, requestHeaders, textTypeBody, cookie);
    }

    public static HttpRequest getHttpRequestURIPath(final String path) {
        final var requestLine = new RequestLine(Method.GET, URI.create(path), new Version(1, 1));
        final var requestHeaders = new RequestHeaders("");
        final var textTypeBody = new TextTypeBody("");
        final var cookie = new Cookie("");
        return new HttpRequest(requestLine, requestHeaders, textTypeBody, cookie);
    }


    public static HttpRequest getHttpRequestBody(final Body body) {
        final var requestLine = new RequestLine(Method.GET, URI.create("/"), new Version(1, 1));
        final var requestHeaders = new RequestHeaders("");
        final var cookie = new Cookie("");
        return new HttpRequest(requestLine, requestHeaders, body, cookie);
    }

    public static HttpResponse getHttpResponse() {
        final var ok = new StatusLine(new Version(1, 1), new StatusCode("OK", 200));
        final var requestHeaders = new ResponseHeader();
        final var textTypeBody = new TextTypeBody("");
        return new HttpResponse(ok, requestHeaders, textTypeBody);
    }
}
