package com.techcourse.controller;

import com.techcourse.controller.model.AbstractController;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import org.apache.coyote.http11.domain.ResourceFinder;
import org.apache.coyote.http11.domain.body.ContentType;
import org.apache.coyote.http11.request.domain.RequestLine;
import org.apache.coyote.http11.request.domain.RequestMethod;
import org.apache.coyote.http11.request.domain.RequestPath;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.paser.LoginQueryParser;
import org.apache.coyote.http11.response.domain.HttpStatus;
import org.apache.coyote.http11.response.maker.HttpResponseMaker;
import org.apache.coyote.http11.response.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String LOGIN_PATH = "/login";
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isStartsWith(LOGIN_PATH) && requestLine.isSameMethod(RequestMethod.GET);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException("해당되는 메서드의 요청을 찾지 못했습니다.");
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        RequestPath requestPath = httpRequest.getRequestPath();

        String resource = ResourceFinder.find(new RequestPath(LOGIN_PATH + "." + ContentType.HTML));
        LoginQueryParser loginQuery = new LoginQueryParser(requestPath.getQueryString());

        User user = userService.findUser(loginQuery.findAccount(), loginQuery.findPassword());
        logger.info(user.toString());

        return HttpResponseMaker.make(resource, ContentType.HTML, HttpStatus.OK);
    }
}
