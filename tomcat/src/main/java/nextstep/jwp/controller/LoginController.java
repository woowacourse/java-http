package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public String run(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpMethod method = httpRequest.getMethod();
        if (method.equals(HttpMethod.GET)) {
            httpResponse.setStatusCode(HttpStatusCode.OK);
            return "/login.html";
        }
        final Map<String, String> requestBody = httpRequest.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final boolean isSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
        if (isSuccess) {
            httpResponse.setStatusCode(HttpStatusCode.FOUND);
            httpResponse.setHeader("Location", "/");
            log.info("로그인 성공! 로그인 아이디: " + account);
            return "/index.html";
        }
        httpResponse.setStatusCode(HttpStatusCode.FOUND);
        httpResponse.setHeader("Location", "/401.html");
        return "/401.html";
    }
}
