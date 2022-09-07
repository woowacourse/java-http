package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Extension;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class RegisterController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        final Path path = request.getPath();
        if (request.isGetMethod()) {
            final String responseBody = ResourceFindUtils
                    .getResourceFile(path.getResource() + Extension.HTML.getExtension());
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType(path.getContentType())
                    .responseBody(responseBody)
                    .build();
        }

        if (request.isPostMethod()) {
            final Map<String, String> params = request.getBody();
            final String account = params.get("account");
            final String password = params.get("password");
            final String email = params.get("email");
            final User user = new User(account, password, email);

            InMemoryUserRepository.save(user);

            return new HttpResponse.Builder()
                    .status(HttpStatus.FOUND)
                    .location("/index.html")
                    .build();
        }

        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }
}
