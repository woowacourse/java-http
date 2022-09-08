package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.request.RequestBody;
import org.apache.coyote.domain.response.ContentType;
import org.apache.coyote.domain.response.HttpResponse;
import org.apache.coyote.domain.response.HttpStatusCode;
import org.apache.coyote.domain.response.RedirectUrl;
import org.apache.coyote.domain.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String URL = "/register";

    @Override
    void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.responseLine(request.getRequestLine().getHttpVersion(), HttpStatusCode.OK)
                .header(ContentType.from(request.getRequestLine().getPath().getFilePath()))
                .responseBody(ResponseBody.from(request.getRequestLine().getPath().getFilePath()));
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.responseLine(request.getRequestLine().getHttpVersion(), HttpStatusCode.FOUND)
                .header(ContentType.from(request.getRequestLine().getPath().getFilePath()))
                .responseBody(ResponseBody.from(request.getRequestLine().getPath().getFilePath()))
                .addRedirectUrlHeader(RedirectUrl.from(register(request)));
    }

    @Override
    public boolean handle(HttpRequest httpRequest) {
        return URL.equals(httpRequest.getRequestLine().getPath().getPath());
    }

    private static String register(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getRequestBody().get("account");
        String password = requestBody.getRequestBody().get("password");
        String email = requestBody.getRequestBody().get("email");
        User user = new User(InMemoryUserRepository.size() + 1L, account, password, email);
        InMemoryUserRepository.save(user);
        return "/index.html";
    }
}
