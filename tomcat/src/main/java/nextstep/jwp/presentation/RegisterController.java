package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ok("/register.html", FileReader.read("/register.html"));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            final String account = httpRequest.getHttpBody("account");
            final String email = httpRequest.getHttpBody("email");
            final String password = httpRequest.getHttpBody("password");
            checkUser(account, email, password);
            return HttpResponse.redirect(StatusCode.FOUND, "/index.html");
        } catch (RuntimeException e) {
            return HttpResponse.internalServerError();
        }
    }

    private void checkUser(final String account, final String email, final String password) {
        if (InMemoryUserRepository.existByAccount(account)) {
            throw new RuntimeException("이미 존재하는 유저입니다.");
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info(user.toString());
    }
}
