package nextstep.jwp.controller.page;

import static org.apache.coyote.http11.common.HttpHeaders.COOKIE_NAME;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.PathUtil;
import nextstep.jwp.util.ResponseBodyUtil;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String COMMA_REGEX = "\\.";
    private static final int FILENAME_INDEX = 0;
    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final int VALUE_INDEX = 1;
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final String USER = "user";

    private LoginController() {
    }

    public static Controller create() {
        return new LoginController();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String uri = request.getUri()
                .split(COMMA_REGEX)[FILENAME_INDEX];
        final Path loginPath = PathUtil.findPathWithExtension(uri, HTML);

        final Session session = SessionManager.findSession(request.getHeaders().getCookie(COOKIE_NAME));
        if (Objects.isNull(session)) {
            response.setStatusLine(HttpStatus.OK);
            response.setHeaders(loginPath);
            response.setResponseBody(ResponseBodyUtil.alter(loginPath));
            return;
        }

        final Path indexPath = PathUtil.findPathWithExtension(INDEX_URI, HTML);

        response.setStatusLine(HttpStatus.FOUND);
        response.setHeaders(indexPath);
        response.setLocation(INDEX_URI + HTML);
        response.setResponseBody(ResponseBodyUtil.alter(indexPath));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final String body = request.getRequestBody();
        final String username = getBodyValue(body, FIRST);
        final String password = getBodyValue(body, SECOND);

        if (InMemoryUserRepository.findByAccountAndPassword(username, password).isEmpty()) {
            final Path path = PathUtil.findPathWithExtension(UNAUTHORIZED_URI, HTML);

            response.setStatusLine(HttpStatus.UNAUTHORIZED);
            response.setHeaders(path);
            response.setResponseBody(ResponseBodyUtil.alter(path));
        }
        final User user = InMemoryUserRepository.findByAccountAndPassword(username, password)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        log.info(user.toString());

        final String uuid = saveSession(user);
        final Path path = PathUtil.findPathWithExtension(INDEX_URI, HTML);

        response.setStatusLine(HttpStatus.FOUND);
        response.setLocation(INDEX_URI + HTML);
        response.setCookie(COOKIE_NAME, uuid);
        response.setResponseBody(ResponseBodyUtil.alter(path));
    }

    private String saveSession(final User user) {
        final String uuid = UUID.randomUUID().toString();
        final Session session = new Session(uuid);
        session.setAttribute(USER, user);
        SessionManager.add(session);
        return uuid;
    }

    private String getBodyValue(final String body, final int index) {
        final String[] parseQuery = body.split(QUERY_DELIMITER);
        return parseQuery[index].split(PARAM_DELIMITER)[VALUE_INDEX];
    }
}
