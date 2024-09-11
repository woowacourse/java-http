package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.TEXT_HTML;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return process(request).build();
    }

    private HttpResponseBuilder process(final HttpRequest request) {
        HttpQueryParams query = request.getQueryParams();
        if (query.isEmpty()) {
            return buildResponse(HttpStatusCode.OK, "/login.html", request);
        }
        return processLogin(request, query);
    }

    private HttpResponseBuilder processLogin(HttpRequest request, HttpQueryParams query) {
        String account = query.get("account");
        String password = query.get("password");

        User user = fetchUserByAccount(account);
        if (user == null || !user.checkPassword(password)) {
            return buildResponse(HttpStatusCode.UNAUTHORIZED, "/401.html", request);
        }

        return buildResponse(HttpStatusCode.FOUND, "/index.html", request);
    }

    private User fetchUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account).orElse(null);
    }

    private HttpResponseBuilder buildResponse(HttpStatusCode statusCode, String resourcePath, HttpRequest request) {
        HttpStatusLine statusLine = new HttpStatusLine(request.getHttpVersion(), statusCode);
        String resource = StaticResourceHandler.handle(resourcePath);
        HttpBody body = new HttpBody(resource);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .body(body)
                .contentType(TEXT_HTML.defaultCharset());
    }
}
