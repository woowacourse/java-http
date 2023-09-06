package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderType.LOCATION;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpResponseStatusLine;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class RequestHandler {

    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String DIRECTORY = "static";
    public static final String LOGIN_RESOURCE = "/login.html";
    public static final String DEFAULT_RESOURCE = "/index.html";
    public static final String UNAUTHORIZED_RESOURCE = "/401.html";
    public static final String REGISTER_RESOURCE = "/register.html";
    public static final String ENTRY_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String EMPTY = "";
    public static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
    public static final String NOT_FOUND_RESOURCE = "/404.html";


    public abstract HttpResponse handle(final HttpRequest httpRequest) throws IOException;

    HttpResponse getPage(HttpRequest httpRequest, String resourcePath, HttpStatusCode statusCode) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), statusCode);

        final HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders();
        httpResponseHeaders.add(CONTENT_TYPE, CONTENT_TYPE_HTML);
        httpResponseHeaders.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        final HttpResponseBody body = new HttpResponseBody(responseBody);

        return new HttpResponse(statusLine, httpResponseHeaders, body);
    }

    HttpResponse getResource(HttpRequest httpRequest, HttpStatusCode code) throws IOException {
        final HttpRequestUri requestUri = httpRequest.getUri();
        final URL resource = getClass().getClassLoader().getResource(DIRECTORY + requestUri.getPath());

        final File file = new File(resource.getFile());
        final String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

        final String responseBody = new String(Files.readAllBytes(file.toPath()));

        final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), code);

        final HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders();
        httpResponseHeaders.add(CONTENT_TYPE, String.format("text/%s;charset=utf-8", extension));
        httpResponseHeaders.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        final HttpResponseBody body = new HttpResponseBody(responseBody);

        return new HttpResponse(statusLine, httpResponseHeaders, body);
    }

    HttpResponse getNotFoundPage(final HttpRequest httpRequest) throws IOException {
        return getPage(httpRequest, DIRECTORY + NOT_FOUND_RESOURCE, NOT_FOUND);
    }

    HttpResponse getRedirectPage(final HttpRequest httpRequest, final String redirectPath, final HttpStatusCode statusCode) {
        final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(httpRequest.getHttpVersion(), statusCode);

        final HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders();
        httpResponseHeaders.add(CONTENT_TYPE, CONTENT_TYPE_HTML);
        httpResponseHeaders.add(CONTENT_LENGTH, String.valueOf(EMPTY.getBytes().length));
        httpResponseHeaders.add(LOCATION, redirectPath);

        final HttpResponseBody body = new HttpResponseBody(EMPTY);

        return new HttpResponse(statusLine, httpResponseHeaders, body);
    }

    Map<String, String> parseRequestBody(final String body) {
        final Map<String, String> requestBody = new HashMap<>();
        Arrays.stream(body.split(ENTRY_DELIMITER))
                .forEach(value -> requestBody.put(value.split(KEY_VALUE_DELIMITER)[KEY_INDEX],
                        value.split(KEY_VALUE_DELIMITER)[VALUE_INDEX]));
        return requestBody;
    }
}
