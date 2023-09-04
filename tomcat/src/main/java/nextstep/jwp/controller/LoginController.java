package nextstep.jwp.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return "/login".equals(httpRequest.getPath())
                && HttpMethod.POST == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        try {
            final String[] split = httpRequest.getBody().split("&");
            final String account = split[0].split("=")[1];
            final String password = split[1].split("=")[1];
            log.info("login request: account=" + account + ", password=" + password);
            final User findUser = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("그런 회원은 없어요 ~"));
            if (!findUser.checkPassword(password)) {
                throw new IllegalArgumentException("비밀번호가 틀렸어요 ~ ");
            }

            log.info("login success!");
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Location", "/index.html");
            return new HttpResponse(
                    "HTTP/1.1",
                    StatusCode.FOUND,
                    headers
            );
        } catch (IllegalArgumentException exception) {
            log.info("login fail! : " + exception.getMessage());
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Location", "/401.html");
            return new HttpResponse(
                    "HTTP/1.1",
                    StatusCode.FOUND,
                    headers
            );
        }
    }
}
