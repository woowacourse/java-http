package nextstep.jwp.controller;

import static nextstep.jwp.utils.FileReader.readFile;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class LoginController implements Controller {
    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        Map<String, String> requestParams = httpRequest.getPath().getRequestParams();
        if (requestParams.size() == 0) {
            return HttpResponse.from(HttpStatus.OK, ContentType.HTML, readFile("/login.html"));
        }

        User user = InMemoryUserRepository.findByAccount(requestParams.get("account"))
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
        if (user.checkPassword(requestParams.get("password"))) {
            return HttpResponse.from(HttpStatus.FOUND, ContentType.HTML, readFile("/index.html"));
        }
        return HttpResponse.from(HttpStatus.NOT_FOUND, ContentType.HTML, readFile("/404.html"));
    }
}
