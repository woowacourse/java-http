package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController implements Controller {
    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        Map<String, String> requestParams = httpRequest.getPath().getRequestParams();
        if (requestParams.size() == 0) {
            return HttpResponse.ok("/login.html");
        }

        User user = InMemoryUserRepository.findByAccount(requestParams.get("account"))
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
        if (user.checkPassword(requestParams.get("password"))) {
            return HttpResponse.found("/index.html");
        }
        return HttpResponse.unAuthorized();
    }
}
