package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
    private static final String DELIMITER = "\r\n";

    @Override
    public void handle(final HttpRequest request, final OutputStream outputStream) {
        final Map<String, String> queryString = request.getQueryString();
        final String account = queryString.get("account");
        final String password = queryString.get("password");

        final Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            final User user = userOpt.get();
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
            }
        }

        URL resource = classLoader.getResource(STATIC_DIRECTORY + "/login.html");
        final File file = new File(resource.getFile());
        try{
            final String response = makeResponse(new String(Files.readAllBytes(file.toPath())));
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makeResponse(final String responseBody) {
        return String.join(DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
