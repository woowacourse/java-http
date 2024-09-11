package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.TEXT_HTML;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;
import org.apache.coyote.http.StaticResourceHandler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return process(request).build();
    }

    private HttpResponseBuilder process(final HttpRequest request) {
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            return get(request);
        }
        if (method == HttpMethod.POST) {
            return post(request);
        }
        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }

    private HttpResponseBuilder get(final HttpRequest request) {
        HttpStatusLine statusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatusCode.OK);
        String resource = StaticResourceHandler.handle("/register.html");
        HttpBody responseBody = new HttpBody(resource);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .body(responseBody)
                .contentType(TEXT_HTML.defaultCharset());
    }

    private HttpResponseBuilder post(final HttpRequest request) {
        HttpStatusLine statusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatusCode.FOUND);
        register(request);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(TEXT_HTML.defaultCharset())
                .location("/index.html");
    }

    private void register(final HttpRequest request) {
        HttpBody body = request.getBody();
        String content = body.getContent();
        HttpQueryParams params = new HttpQueryParams(content);
        User user = new User(params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(user);
    }
}
