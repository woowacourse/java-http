package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.SessionManager;

import java.io.IOException;

import static org.apache.coyote.http11.response.Header.LOCATION;
import static org.apache.coyote.http11.Method.GET;
import static org.apache.coyote.http11.Method.POST;
import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;
import static org.apache.coyote.http11.response.StatusCode.UNAUTHORIZED;

public class LoginController implements Controller {

    private static final String INDEX = "/index.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        String method = requestReader.getMethod();

        if (GET.matches(method)) {
            return loginPage(requestReader);
        }
        if (POST.matches(method)) {
            return tryLogin(requestReader);
        }

        return null;
    }

    private Response loginPage(RequestReader requestReader) throws IOException {
        if (requestReader.hasSessionId()) {
            return new Response(FOUND)
                    .addHeader(LOCATION, INDEX)
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
                    .addHeader(LOCATION, INDEX)
                    .setCookie(sessionId);
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

        return SessionManager.getSessionId(user);
    }
}
