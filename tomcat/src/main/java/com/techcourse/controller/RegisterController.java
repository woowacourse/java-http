package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {
    public static final String HTML_SUFFIX = ".html";
    public static final String INDEX_PAGE = "/index.html";
    public static final String ACCESS_DENIED_PAGE = "/401.html";
    public static final String ACCOUNT_PARAM_NAME = "account";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String EMAIL_PARAM_NAME = "email";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final Map<String, String> userInfos = request.parseRequestQuery();

        final String account = userInfos.get(ACCOUNT_PARAM_NAME);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.found();
            response.sendRedirect(ACCESS_DENIED_PAGE);
            return;
        }
        persistUser(userInfos);

        response.found();
        response.sendRedirect(INDEX_PAGE);
    }

    private void persistUser(Map<String, String> userInfos) {
        final User user = new User(userInfos.get(ACCOUNT_PARAM_NAME),
                userInfos.get(PASSWORD_PARAM_NAME),
                userInfos.get(EMAIL_PARAM_NAME)
        );
        InMemoryUserRepository.save(user);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var path = request.getRequestPath();
        response.ok();
        response.setContentType(FileType.HTML);
        response.setContentOfResources(path + HTML_SUFFIX);
    }
}
