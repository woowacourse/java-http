package org.apache.catalina.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterHandler extends AbstractController {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    void doGet(final Http11Request request, final Http11Response response) throws Exception {
        final byte[] fileContent = readFile("/register.html");

        response.setResponse(HttpStatus.OK, fileContent, HttpContentType.HTML.getValue());
    }

    @Override
    void doPost(final Http11Request request, final Http11Response response) throws Exception {
        final Map<String, String> urlEncodedResponseBody = request.getBodyByContentType(HttpContentType.URL);

        final String account = urlEncodedResponseBody.get("account");
        final String email = urlEncodedResponseBody.get("email");
        final String password = urlEncodedResponseBody.get("password");

        validateExistingSession(account);

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final Session session = createSession(user);

        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=Strict", session.getId()));

        response.setRedirectResponse("/index.html", headers);
    }

    private static void validateExistingSession(final String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException(String.format("Already signed up : account = %s", account));
        }
    }

    private Session createSession(final User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);

        return session;
    }
}
