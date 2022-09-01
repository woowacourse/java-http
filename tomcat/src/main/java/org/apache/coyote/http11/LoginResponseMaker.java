package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.utils.QueryParamsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginResponseMaker implements ResponseMaker {

    private static final Logger log = LoggerFactory.getLogger(LoginResponseMaker.class);

    @Override
    public String createResponse(final String requestUrl)
            throws URISyntaxException, IOException {
        final HashMap<String, String> loginData = QueryParamsParser.parseLoginParams(requestUrl);
        validateAccount(loginData);
        final URL resource =
                this.getClass().getClassLoader().getResource("static" + "/login.html");
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + ContentType.HTML + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public void validateAccount(final HashMap<String, String> loginData) {
        final String account = loginData.get("account");
        final String password = loginData.get("password");
        final User accessUser = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        validatePassword(password, accessUser);
        log.info("user = {}", accessUser);
    }

    private void validatePassword(final String password, final User accessUser) {
        if (!accessUser.checkPassword(password)) {
            throw new IllegalArgumentException("존재하지 않는 계정입니다.");
        }
    }
}
