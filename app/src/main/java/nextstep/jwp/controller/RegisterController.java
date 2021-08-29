package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        log.debug("Register - HTTP GET Request");

        response.setLine(HttpStatus.OK);

        return new View("/register");
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        log.debug("Register - HTTP POST Request");

        // 회원가입 완료
        response.setLine(HttpStatus.FOUND);

        // 회원가입 실패 -> 예외 발생

        return new View("/register");
    }
}
