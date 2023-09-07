package nextstep.jwp.controller.page;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.FileContent;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseLine;

public class LoginGetPageController implements Controller {

    private static final String STATIC = "/static";

    private LoginGetPageController() {
    }

    public static Controller create() {
        return new LoginGetPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final String[] splitUri = uri.split("\\.");
        URL url;
        if (splitUri.length == 1) {
            url = HttpResponse.class.getClassLoader()
                    .getResource(STATIC + FileContent.findPage(uri) + ".html");

            final Path path = new File(url.getPath()).toPath();

            final byte[] content = Files.readAllBytes(path);

            final HttpHeaders headers = HttpHeaders.createResponse(path);
            final String responseBody = new String(content);

            final Session session = SessionManager.findSession(request.getHeaders().getCookie("JSESSIONID"));
            if (uri.equals("/login") && Objects.nonNull(session)) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + "/index" + ".html");
                final Path loginPath = new File(url.getPath()).toPath();

                final byte[] loginContent = Files.readAllBytes(loginPath);
                final String loginResponseBody = new String(loginContent);

                headers.setHeader("Location", "/index.html");

                return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, loginResponseBody);
            }
            return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
        } else {
            url = HttpResponse.class.getClassLoader()
                    .getResource(STATIC + uri);

            final Path path = new File(url.getPath()).toPath();

            final byte[] content = Files.readAllBytes(path);

            final HttpHeaders headers = HttpHeaders.createResponse(path);
            final String responseBody = new String(content);

            return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
        }
    }
}
