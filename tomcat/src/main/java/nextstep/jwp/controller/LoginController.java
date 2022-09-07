package nextstep.jwp.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Extension;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class LoginController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isGetMethod()) {
            return doGet(request);
        }

        if (request.isPostMethod()) {
            return doPost(request);
        }

        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }

    private HttpResponse doGet(HttpRequest request) {
        final Path path = request.getPath();
        final String responseBody = ResourceFindUtils
                .getResourceFile(path.getResource() + Extension.HTML.getExtension());

        return new HttpResponse.Builder()
                .status(HttpStatus.OK)
                .contentType(path.getContentType())
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse doPost(HttpRequest request) {
        login(request.getBody());

        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location("/index.html")
                .cookie("JSESSIONID=" + UUID.randomUUID())
                .build();
    }

    private void login(Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailedException::new);

        user.checkPassword(password);
    }
}
