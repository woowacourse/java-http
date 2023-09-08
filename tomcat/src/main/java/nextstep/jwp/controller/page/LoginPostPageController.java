package nextstep.jwp.controller.page;

import static nextstep.jwp.controller.FileContent.HTML;
import static nextstep.jwp.controller.FileContent.INDEX_URI;
import static nextstep.jwp.controller.FileContent.STATIC;
import static nextstep.jwp.controller.FileContent.UNAUTHORIZED_URI;
import static org.apache.coyote.http11.common.HttpHeaders.COOKIE_NAME;
import static org.apache.coyote.http11.common.HttpHeaders.LOCATION;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPostPageController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginPostPageController.class);
    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final int VALUE_INDEX = 1;
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final String USER = "user";

    private LoginPostPageController() {
    }

    public static Controller create() {
        return new LoginPostPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final String body = request.getRequestBody();
        final String[] parseQuery = body.split(QUERY_DELIMITER);
        final String username = parseQuery[FIRST].split(PARAM_DELIMITER)[VALUE_INDEX];
        final String password = parseQuery[SECOND].split(PARAM_DELIMITER)[VALUE_INDEX];

        if (InMemoryUserRepository.findByAccountAndPassword(username, password).isEmpty()) {
            final URL unauthorizedUrl = HttpResponse.class.getClassLoader()
                    .getResource(STATIC + UNAUTHORIZED_URI + HTML);
            final Path path = new File(unauthorizedUrl.getPath()).toPath();

            final byte[] content = Files.readAllBytes(path);

            final HttpHeaders headers = HttpHeaders.createResponse(path);
            final String responseBody = new String(content);

            return new HttpResponse(ResponseStatusLine.create(HttpStatus.UNAUTHORIZED), headers, responseBody);
        }

        final URL indexUrl = HttpResponse.class.getClassLoader()
                .getResource(STATIC + INDEX_URI + HTML);
        final Path path = new File(indexUrl.getPath()).toPath();

        final User user = InMemoryUserRepository.findByAccountAndPassword(username, password)
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 에러입니다."));
        log.info(user.toString());

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        final String uuid = UUID.randomUUID().toString();

        final Session session = new Session(uuid);
        session.setAttribute(USER, user);
        SessionManager.add(session);

        headers.setCookie(COOKIE_NAME, uuid);
        headers.setHeader(LOCATION, INDEX_URI + HTML);
        final String responseBody = new String(content);

        return new HttpResponse(ResponseStatusLine.create(HttpStatus.FOUND), headers, responseBody);
    }
}
