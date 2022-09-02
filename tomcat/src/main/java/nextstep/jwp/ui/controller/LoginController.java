package nextstep.jwp.ui.controller;

import http.HttpRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String SUPPORT_URI = "/login";

    @Override
    public boolean support(final String requestUri) {
        return Objects.equals(SUPPORT_URI, requestUri);
    }

    @Override
    public String service(final HttpRequest httpRequest) {
        final var loginHtml = String.format("%s.html", httpRequest.getRequestURIWithoutQueryParams());
        final var resource = getClass().getClassLoader().getResource(String.format("static%s", loginHtml));
        final var path = new File(resource.getFile()).toPath();

        logAccountInfo(httpRequest);

        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void logAccountInfo(final HttpRequest httpRequest) {
        var account = (String) httpRequest.getParameter("account");
        if (Objects.isNull(account)) {
            account = "";
        }

        final var userByAccount = InMemoryUserRepository.findByAccount(account)
                .orElse(null);
        log.info("found account info : {}", userByAccount);
    }
}
