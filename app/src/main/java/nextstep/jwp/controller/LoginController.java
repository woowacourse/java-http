package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;
import nextstep.jwp.HttpStatus;
import nextstep.jwp.StaticFileReader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController implements Controller {


    @Override
    public HttpResponse get(HttpRequest request) {
        StaticFileReader staticFileReader = new StaticFileReader();
        Map<String, String> queryParams = request.extractURIQueryParams();
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        if (!Objects.isNull(account) && !Objects.isNull(password)) {
            User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));
            if (user.checkPassword(password)) {
                return new HttpResponse(HttpStatus.FOUND);
            }
            String htmlOf401 = staticFileReader.read("static/401.html");
            return new HttpResponse(HttpStatus.UNAUTHORIZED, htmlOf401);
        }
        String htmlOfLogin = staticFileReader.read("static/login.html");
        return new HttpResponse(HttpStatus.OK, htmlOfLogin);
    }
}
