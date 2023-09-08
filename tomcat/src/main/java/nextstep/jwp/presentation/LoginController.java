package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.RequestReader;
import org.apache.coyote.http11.Response;
import org.apache.coyote.http11.SessionManager;

import java.io.IOException;
import java.util.UUID;

import static org.apache.coyote.http11.Header.LOCATION;
import static org.apache.coyote.http11.StatusCode.FOUND;
import static org.apache.coyote.http11.StatusCode.OK;
import static org.apache.coyote.http11.StatusCode.UNAUTHORIZED;

public class LoginController implements Controller {

    private static final String INDEX = "/index.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        String method = requestReader.getMethod();
        String uri = requestReader.getUri();

        if (method.equalsIgnoreCase("GET") && uri.equals("/login.html")) {
            return loginPage(requestReader);
        }
        if (method.equalsIgnoreCase("POST") && uri.equals("/login.html")) {
            return tryLogin(requestReader);
        }
        if (method.equalsIgnoreCase("GET") && uri.equals("/register.html")) {
            return registerPage(requestReader);
        }
        if (method.equalsIgnoreCase("POST") && uri.equals("/register.html")) {
            return register(requestReader);
        }
        return null;
    }

    private Response loginPage(RequestReader requestReader) throws IOException {
        if (requestReader.isSessionExits()) {
            return new Response(FOUND)
                    .addHeader(LOCATION.getName(), INDEX)
                    .addBaseHeader(requestReader.getContentType());
        }
        return new Response(OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByFile(requestReader.getUri());

    }

    private Response tryLogin(RequestReader requestReader) throws IOException {
        try {
            String sessionId = login(requestReader);
            return new Response(FOUND)
                    .addBaseHeader(requestReader.getContentType())
                    .addHeader(LOCATION.getName(), INDEX)
                    .addCookieBySession(requestReader.isSessionExits(), sessionId);
        } catch (IllegalArgumentException e) {
            return new Response(UNAUTHORIZED)
                    .addBaseHeader(requestReader.getContentType())
                    .createBodyByFile(UNAUTHORIZED_HTML);
        }
    }

    private String login(RequestReader requestReader) {
        User user = InMemoryUserRepository.findByAccount(requestReader.getBodyValue("account"))
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자가 존재하지 않습니다"));

        if (!user.checkPassword(requestReader.getBodyValue("password"))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        String sessionId = UUID.randomUUID().toString();
        SessionManager.addSession(sessionId, user);

        return sessionId;
    }

    private Response registerPage(RequestReader requestReader) throws IOException {
        return new Response(OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByFile(requestReader.getUri());
    }

    private Response register(RequestReader requestReader) throws IOException {
        InMemoryUserRepository.save(new User(
                requestReader.getBodyValue("account"),
                requestReader.getBodyValue("email"),
                requestReader.getBodyValue("password")
        ));
        return new Response(FOUND)
                .createBodyByFile(INDEX)
                .addHeader(LOCATION.getName(), INDEX)
                .addBaseHeader(requestReader.getContentType());
    }
}
