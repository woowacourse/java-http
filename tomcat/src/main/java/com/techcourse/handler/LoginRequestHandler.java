package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.NotFoundException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpMethod;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.http.response.Location;
import com.techcourse.http.response.ResponseBody;
import com.techcourse.model.User;
import java.util.Map;

public class LoginRequestHandler {

    private final HttpVersion httpVersion;

    public LoginRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpResponse handleLoginRequest(final HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, ResponseBody.createBy(httpRequest));
        }
        if (httpMethod == HttpMethod.POST) {
            Map<String, String> requestBodyValues = httpRequest.getRequestBody();

            String account = requestBodyValues.get("account");
            String password = requestBodyValues.get("password");

            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

            if (user.checkPassword(password)) {
                return HttpResponse.found(httpVersion, new Location("/index.html"), ContentType.APPLICATION_JSON);
            }
            return HttpResponse.found(httpVersion, new Location("/401.html"), ContentType.APPLICATION_JSON);
        }

        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }
}
