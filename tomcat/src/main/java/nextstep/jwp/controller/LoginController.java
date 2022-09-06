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
        if (httpRequest.isGet()) {
            return doGet();
        }
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }
        return HttpResponse.notFound();
    }

    private HttpResponse doGet() throws IOException {
        return HttpResponse.ok("/login.html");
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        Map<String, String> requestBody = httpRequest.getBody();

        User user = InMemoryUserRepository.findByAccount(requestBody.get("account"))
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
        if (user.checkPassword(requestBody.get("password"))) {
            return HttpResponse.found("/index.html");
        }
        return HttpResponse.unAuthorized();
    }
}
