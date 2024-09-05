package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.AbstractController;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.util.HttpResponseBuilder;

public final class MemberController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse httpResponse) {

        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, httpResponse);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, httpResponse);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
        String account = request.getParams("account");
        String password = request.getParams("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnauthorizedException::new);
        if (user.checkPassword(password)) {
            httpResponse.setStatusCode(HttpStatus.FOUND.statusCode());
            httpResponse.setStatusMessage(HttpStatus.FOUND.statusMessage());
            httpResponse.addHeaders("Location", "/index.html");
            HttpResponseBuilder.buildDefault(httpResponse);
            return;
        }
        httpResponse.setStatusCode(HttpStatus.FOUND.statusCode());
        httpResponse.setStatusMessage(HttpStatus.FOUND.statusMessage());
        httpResponse.addHeaders("Location", "/401.html");
        HttpResponseBuilder.buildDefault(httpResponse);
    }
}
