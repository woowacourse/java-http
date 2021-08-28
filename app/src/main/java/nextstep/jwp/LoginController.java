package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        if (!httpRequest.toURI().hasQuery()) {
            URL resource = getClass().getClassLoader().getResource("static" + getResource() + ".html");
            Path path = Paths.get(resource.getPath());
            byte[] bytes = Files.readAllBytes(path);

            return new HttpResponse(HttpStatus.OK, bytes);
        } else {
            final Map<String, String> queryInfo = extractQuery(httpRequest.toURI().getQuery());
            final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                    .orElseThrow(() -> new UserNotFoundException(queryInfo.get("account")));
            if (user.checkPassword(queryInfo.get("password"))) {
                log.info("Login successful!");

                URL resource = getClass().getClassLoader().getResource("static" + "/index.html");
                Path path = Paths.get(resource.getPath());
                byte[] bytes = Files.readAllBytes(path);

                return new HttpResponse(HttpStatus.FOUND, bytes);
            } else {
                log.info("Login failed");

                URL resource = getClass().getClassLoader().getResource("static" + "/401.html");
                Path path = Paths.get(resource.getPath());
                byte[] bytes = Files.readAllBytes(path);

                return new HttpResponse(HttpStatus.UNAUTHORIZED, bytes);
            }
        }
    }
}
