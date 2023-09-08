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

        if (method.equalsIgnoreCase("GET") && uri.equals("/login")) {
            return loginPage(requestReader);
        }
        if (method.equalsIgnoreCase("POST") && uri.equals("/login")) {
            return tryLogin(requestReader);
        }
        if (method.equalsIgnoreCase("GET") && uri.equals("/register")) {
            return registerPage(requestReader);
        }
        if (method.equalsIgnoreCase("POST") && uri.equals("/register")) {
            return register(requestReader);
        }
        return null;
    }

    private Response loginPage(RequestReader requestReader) throws IOException {
        if (requestReader.isSessionExits()) {
            return new Response(requestReader, OK)
                    .addBaseHeader()
                    .createBodyByFile(requestReader.getUri());
        }
        return new Response(requestReader, FOUND)
                .createBodyByFile(INDEX)
                .addHeader(LOCATION.getName(), INDEX)
                .addBaseHeader();
    }

    private Response tryLogin(RequestReader requestReader) throws IOException {
        try {
            String sessionId = login(requestReader);
            return new Response(requestReader, FOUND)
                    .createBodyByFile(INDEX)
                    .addHeader(LOCATION.getName(), INDEX)
                    .addCookieBySession(sessionId)
                    .addBaseHeader();
        } catch (IllegalArgumentException e) {
            return new Response(requestReader, UNAUTHORIZED)
                    .addBaseHeader()
                    .createBodyByFile(UNAUTHORIZED_HTML);
        }
    }

    private String login(RequestReader requestReader) {
        User find = InMemoryUserRepository.findByAccount(requestReader.getBodyValue("account"))
                .filter(user -> user.checkPassword(requestReader.getBodyValue("password")))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다"));

        String sessionId = UUID.randomUUID().toString();
        SessionManager.addSession(sessionId, find);

        return sessionId;
    }

    private Response registerPage(RequestReader requestReader) throws IOException {
        return new Response(requestReader, OK)
                .addBaseHeader()
                .createBodyByFile(requestReader.getUri());
    }

    private Response register(RequestReader requestReader) throws IOException {
        InMemoryUserRepository.save(new User(
                requestReader.getBodyValue("account"),
                requestReader.getBodyValue("password"),
                requestReader.getBodyValue("email")
        ));
        return new Response(requestReader, FOUND)
                .createBodyByFile(INDEX)
                .addHeader(LOCATION.getName(), INDEX)
                .addBaseHeader();
    }
}
