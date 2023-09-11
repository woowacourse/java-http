package nextstep.jwp.controller;

import common.http.AbstractController;
import common.http.Cookies;
import common.http.Request;
import common.http.Response;
import common.http.Session;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import static common.http.HttpStatus.FOUND;
import static common.http.HttpStatus.OK;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) {
        // 로그아웃이 없으므로 모든 요청에 대해 진행합니다.
        response.addVersionOfTheProtocol(request.getVersionOfTheProtocol());
        response.addHttpStatus(OK);
        response.addStaticResourcePath("/register.html");
    }

    @Override
    protected void doPost(Request request, Response response) {
        if (InMemoryUserRepository.findByAccount(request.getAccount()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        User user = new User(request.getAccount(), request.getPassword(), request.getEmail());
        InMemoryUserRepository.save(user);

        Session session = request.getSession(true);
        session.setAttribute("user", user);
        response.addVersionOfTheProtocol(request.getVersionOfTheProtocol());
        response.addHttpStatus(FOUND);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.sendRedirect("/index.html");
    }
}
