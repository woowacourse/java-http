package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.NotFoundException;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.model.User;
import java.util.Map;

public class LoginRequestHandler {

    private final HttpVersion httpVersion;

    public LoginRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpResponse handleLoginRequest(final HttpRequest httpRequest) {
        Map<String, String> requestParams = httpRequest.getRequestParams();

        String account = requestParams.get("account");
        String password = requestParams.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        if (user.checkPassword(password)) {
            return new HttpResponse(httpVersion, HttpStatus.FOUND, "/index.html", ContentType.APPLICATION_JSON, "");
        }

        return new HttpResponse(httpVersion, HttpStatus.UNAUTHORIZED, "/401.html", ContentType.APPLICATION_JSON, "");
    }
}
