package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.util.FileReader;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        register(httpRequest);
        httpResponse.found()
                .location("/index.html")
                .flushBuffer();
    }

    private void register(final HttpRequest httpRequest) {
        MessageBody messageBody = httpRequest.getMessageBody();
        String account = messageBody.getParameter("account");
        String password = messageBody.getParameter("password");
        String email = messageBody.getParameter("email");
        if (!InMemoryUserRepository.isRegistrable(account)) {
            throw new IllegalArgumentException("Account already exists");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String uri = httpRequest.getUri();
        String responseBody = FileReader.read(uri + ".html");
        httpResponse.ok()
                .contentType(ContentType.from(uri))
                .body(responseBody)
                .flushBuffer();
    }
}
