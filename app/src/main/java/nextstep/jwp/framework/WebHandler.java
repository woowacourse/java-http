package nextstep.jwp.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebHandler.class.getSimpleName());

    private WebHandler() {
    }

    public static String run(final InputStream inputStream) throws IOException {
        final RequestHeader request = RequestHeader.from(inputStream);
        final URL resource = request.resource();

        if (request.url().equals("login.html")) {
            final Map<String, String> queryParam = request.queryParam();

            final Optional<User> account = InMemoryUserRepository.findByAccount(queryParam.get("account"));
            final User user = account.orElseThrow(IllegalAccessError::new);
            logger.info(user.toString());
        }

        assert resource != null;
        final Path path = new File(resource.getPath()).toPath();
        final String content = Files.readString(path);

        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + content.getBytes().length + " ",
            "",
            content);
    }

}
