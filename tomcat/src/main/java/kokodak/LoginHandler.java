package kokodak;

import static kokodak.HttpStatusCode.FOUND;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasQueryString()) {
            final RequestTarget requestTarget = httpRequest.getRequestTarget();
            final User user = InMemoryUserRepository.findByAccount(requestTarget.queryString("account"))
                                                    .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
            if (user.checkPassword(requestTarget.queryString("password"))) {
                return HttpResponse.builder()
                                   .httpStatusCode(FOUND)
                                   .header("Location", "http://localhost:8080/index.html")
                                   .header("Content-Type", "text/html;charset=utf-8")
                                   .build();
            } else {
                return HttpResponse.builder()
                                   .httpStatusCode(FOUND)
                                   .header("Location", "http://localhost:8080/401.html")
                                   .header("Content-Type", "text/html;charset=utf-8")
                                   .build();
            }
        } else {
            final String fileName = "static/login.html";
            final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
            final Path path = new File(resourceUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));
            return HttpResponse.builder()
                               .header("Content-Type", "text/html;charset=utf-8")
                               .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                               .body(responseBody)
                               .build();
        }
    }
}
