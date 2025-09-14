package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.exception.Http4xxException;
import org.apache.catalina.web.controller.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends AbstractController {

    public static final String ENDPOINT = "/register";

    private static final String REDIRECTION_PATH = "/index.html";

    public final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::doGet);
        this.addCommand(HttpMethod.POST, this::doPost);
    }

    public String doGet(final Http11Request request, final Http11Response response) {
        return ENDPOINT;
    }

    public String doPost(final Http11Request request, final Http11Response response) {
        final String account = request.getBodyValueByKey("account");
        final String password = request.getBodyValueByKey("password");
        final String email = request.getBodyValueByKey("email");

        if (!isRequestBodyValid(account, password, email)) {
            throw new Http4xxException("잘못된 요청입니다.", response, HttpStatus.BAD_REQUEST);
        }
        userService.saveUser(response, account, password, email);

        response.setState(HttpStatus.Found);
        return REDIRECTION_PATH;
    }

    private boolean isRequestBodyValid(final String account, final String password, final String email) {
        return StringUtils.isNotBlank(account)
                && StringUtils.isNotBlank(password)
                && StringUtils.isNotBlank(email);
    }
}
