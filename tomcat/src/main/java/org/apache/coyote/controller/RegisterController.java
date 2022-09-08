package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.request.RequestBody;
import org.apache.coyote.domain.response.statusline.ContentType;
import org.apache.coyote.domain.response.HttpResponse;
import org.apache.coyote.domain.response.statusline.HttpStatusCode;
import org.apache.coyote.domain.response.RedirectUrl;
import org.apache.coyote.domain.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String URL = "/register";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String INDEX_HTML = "/index.html";
    private static final long INCREMENT_ID = 1L;

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
                .header(RedirectUrl.from(register(request)));
    }

    @Override
    public boolean handle(HttpRequest httpRequest) {
        return URL.equals(httpRequest.getRequestLine().getPath().getPath());
    }

    private static String register(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getRequestBody().get(ACCOUNT_KEY);
        String password = requestBody.getRequestBody().get(PASSWORD_KEY);
        String email = requestBody.getRequestBody().get(EMAIL_KEY);
        User user = new User(InMemoryUserRepository.size() + INCREMENT_ID, account, password, email);
        InMemoryUserRepository.save(user);
        return INDEX_HTML;
    }
}
