package org.apache.coyote.dynamichandler;

import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.HttpSession;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.statichandler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler extends AbstractHandler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void doGet(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        HttpSession httpSession = httpRequest.getHttpSession();
        User user = (User) httpSession.get("user");

        if (!Objects.isNull(user)) {
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.sendRedirect("/index.html");
            return;
        }

        try {
            String body = findPage(httpRequest.getPath());

            httpResponse.setStatus(OK);
            httpResponse.setHeader(CONTENT_TYPE.getValue(), HTML.getValue());
            httpResponse.setHeader(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
            httpResponse.setResponseBody(body);
        } catch (IOException e) {
            new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).service(httpRequest, httpResponse);
        }
    }

    public String findPage(String path) throws IOException {
        File file = new File(getClass()
                .getClassLoader()
                .getResource(DEFAULT_DIRECTORY_PATH + path + HTML.getExtension())
                .getFile()
        );

        return new String(Files.readAllBytes(file.toPath()));
    }

    @Override
    public void doPost(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        String account = httpRequest.getBodyAttribute("account");
        String password = httpRequest.getBodyAttribute("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        httpResponse.setStatus(FOUND);

        if (user.isPresent() && user.get().checkPassword(password)) {
            HttpSession httpSession = httpRequest.getHttpSession(true);
            httpResponse.addCookie("JSESSIONID", httpSession.getId());
            httpResponse.sendRedirect("/index.html");
            httpSession.add("user", user.get());
            log.info("로그인 성공! 아이디 : {}", account);
            return;
        }

        httpResponse.sendRedirect("/401.html");
    }

}
