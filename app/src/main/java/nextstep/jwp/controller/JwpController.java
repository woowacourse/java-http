package nextstep.jwp.controller;

import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

public class JwpController {
    private static final Logger log = LoggerFactory.getLogger(JwpController.class);

    private final Map<String, Function<String, Map<HttpStatus, String>>> mappedFunction;
    private final PageController pageController = new PageController();

    public JwpController() {
        this.mappedFunction = new HashMap<>();
        this.mappedFunction.put("login", this::postLogin);
        this.mappedFunction.put("register", this::postRegister);
    }

    private Map<HttpStatus, String> postRegister(final String request) {
        try {
            String requestBody = request.split(" ")[1];
            List<String> params = Arrays.asList(requestBody.split("&"));
            User user = UserService.registerUser(params);
            log.info("회원가입된 유저 : " + user.toString());
            return pageController.mapResponse(Optional.of(HttpStatus.CREATED), "index");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return pageController.mapResponse(Optional.of(HttpStatus.BAD_REQUEST), "register");
        }
    }

    private Map<HttpStatus, String> postLogin(final String queryString) {
        try {
            List<String> params = Arrays.asList(queryString.split("&"));
            User user = UserService.findUser(params);
            log.info("로그인한 유저 : " + user.toString());
            return pageController.mapResponse(Optional.of(HttpStatus.FOUND), "index");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return pageController.mapResponse(Optional.of(HttpStatus.BAD_REQUEST), "401");
        }
    }

    public Map<HttpStatus, String> mapResponse(final String request) {
        return this.mappedFunction.keySet().stream()
                .filter(request::contains)
                .map(s -> this.mappedFunction.get(s).apply(request))
                .findAny()
                .orElse(Map.of(HttpStatus.NOT_FOUND, "해당 페이지가 존재하지 않습니다."));
    }
}
