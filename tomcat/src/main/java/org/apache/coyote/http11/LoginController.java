package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller{

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final String STATIC_DIRECTORY = "static";

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.hasQueryString()) {
            final Map<String, String> queryString = request.getQueryString();
            if (queryString.isEmpty() || queryString.size() != 2) {
                return HttpResponse.toUnauthorized();
            }
            final String account = queryString.get("account");
            final String password = queryString.get("password");
            final boolean loginSuccess = login(account, password);
            if (loginSuccess) {
                return new HttpResponse(StatusCode.FOUND, "text/html", HttpResponse.indexResponseBody());
            }
            return HttpResponse.toUnauthorized();
        }


        URL resource = classLoader.getResource(STATIC_DIRECTORY + "/login.html");
        final File file = new File(resource.getFile());
        try{
            return new HttpResponse(StatusCode.OK, "text/html", new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean login(final String account, final String password) {
        final Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            final User user = userOpt.get();
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
                return true;
            }
        }
        return false;
    }
}
