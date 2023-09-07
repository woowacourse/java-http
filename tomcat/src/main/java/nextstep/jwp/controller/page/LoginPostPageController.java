package nextstep.jwp.controller.page;

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
import org.apache.coyote.http11.response.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPostPageController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginPostPageController.class);
    private static final String STATIC = "/static";

    private LoginPostPageController() {
    }

    public static Controller create() {
        return new LoginPostPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final String body = request.getRequestBody();
        final String[] parseQuery = body.split("&");
        final String username = parseQuery[0].split("=")[1];
        final String password = parseQuery[1].split("=")[1];

        URL url;

        if (InMemoryUserRepository.findByAccountAndPassword(username, password).isEmpty()) {
            url = HttpResponse.class.getClassLoader()
                    .getResource(STATIC + "/401" + ".html");
            final Path path = new File(url.getPath()).toPath();

            final byte[] content = Files.readAllBytes(path);

            final HttpHeaders headers = HttpHeaders.createResponse(path);
            final String responseBody = new String(content);

            return new HttpResponse(ResponseLine.create(HttpStatus.UNAUTHORIZED), headers, responseBody);
        } else {
            url = HttpResponse.class.getClassLoader()
                    .getResource(STATIC + "/index" + ".html");
            final Path path = new File(url.getPath()).toPath();

            final User user = InMemoryUserRepository.findByAccountAndPassword(username, password)
                    .orElseThrow(() -> new IllegalArgumentException("알 수 없는 에러입니다."));
            log.info(user.toString());

            final byte[] content = Files.readAllBytes(path);

            final HttpHeaders headers = HttpHeaders.createResponse(path);
            final String uuid = UUID.randomUUID().toString();
            headers.setCookie("JSESSIONID", uuid);
            final Session session = new Session(uuid);
            session.setAttribute("user", user);
            SessionManager.add(session);

            headers.setHeader("Location", "/index.html");
            final String responseBody = new String(content);

            return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, responseBody);
        }
    }
}
