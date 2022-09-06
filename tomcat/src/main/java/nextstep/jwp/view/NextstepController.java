package nextstep.jwp.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.http.BasicHttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.info.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;

@Controller
public class NextstepController {

    private static final Logger log = LoggerFactory.getLogger(NextstepController.class);
    private static final String WELCOME_MESSAGE = "Hello world!";

    @RequestMapping(method = HttpMethod.GET, uri = "/")
    public HttpResponse hello(final HttpRequest httpRequest) {
        return BasicHttpResponse.from(WELCOME_MESSAGE);
    }

    @RequestMapping(method = HttpMethod.GET, uri = "/login")
    public HttpResponse login(final HttpRequest httpRequest) {
        final var loginHtml = String.format("%s.html", httpRequest.getRequestURIWithoutQueryParams());
        final var resource = getClass().getClassLoader().getResource(String.format("static%s", loginHtml));
        final var path = new File(resource.getFile()).toPath();

        logAccountInfo(httpRequest);

        try {
            return BasicHttpResponse.from(new String(Files.readAllBytes(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logAccountInfo(final HttpRequest httpRequest) {
        var account = (String) httpRequest.getParameter("account");
        var password = (String) httpRequest.getParameter("password");
        if (Objects.isNull(account)) {
            account = "";
        }

        InMemoryUserRepository.findByAccountAndPassword(account, password)
                .ifPresent(member -> log.info("found account info : {}", member));
    }
}
