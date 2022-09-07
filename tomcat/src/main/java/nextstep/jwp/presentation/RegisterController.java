package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public HttpResponse process(HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return HttpResponse.ok("/register.html", FileReader.read("/register.html"));
        }
        try {
            final String account = httpRequest.getHttpBody("account");
            final String email = httpRequest.getHttpBody("email");
            final String password = httpRequest.getHttpBody("password");
            checkUser(account, email, password);
            return HttpResponse.found("/index.html", FileReader.read("/index.html"));
        } catch (RuntimeException e) {
            return HttpResponse.unauthorized("/500.html", FileReader.read("/500.html"));
        }
    }

    private void checkUser(final String account, final String email, final String password) {
        if (InMemoryUserRepository.existByAccount(account)) {
            throw new RuntimeException("이미 존재하는 유저입니다.");
        }
        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
        log.info(user.toString());
    }
}
