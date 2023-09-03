package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.RequestMethod;
import org.apache.coyote.http11.common.ResponseStatus;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        if (request.getRequestMethod() == RequestMethod.GET) {
            String redirectionFile = "login.html";
            Cookie cookie = request.parseCookie();
            String jsessionid = cookie.findByKey("JSESSIONID");

            if (jsessionid != null && isLoginUser(jsessionid)) {
                redirectionFile = "index.html";
            }

            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("static/" + redirectionFile);

            File file = new File(resource.getFile());
            String fileData = new String(Files.readAllBytes(file.toPath()));

            List<String> headers = List.of(
                    String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                    String.join(" ", "Content-Length:", String.valueOf(fileData.getBytes().length))
            );

            return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, fileData);
        }
        if (request.getRequestMethod() == RequestMethod.POST) {
            List<String> headers = new ArrayList<>();
            headers.add(String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()));
            headers.add(String.join(" ", "Content-Length:", String.valueOf("".getBytes().length)));

            Optional<User> userResult = findUser(request);

            if (userResult.isPresent()) {
                return loginSuccessResponse(headers, userResult);
            }
            return loginFailResponse(headers);
        }
        throw new UnsupportedOperationException("get, post만 가능합니다.");
    }

    private boolean isLoginUser(String jsessionid) throws IOException {
        Session session = SessionManager.findSession(UUID.fromString(jsessionid));
        return session != null;
    }

    private ResponseEntity loginSuccessResponse(List<String> headers, Optional<User> userResult) {
        User user = userResult.get();
        headers.add(String.join(" ", "Location: index.html"));
        ResponseEntity responseEntity = new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");
        UUID uuid = createSesstion(user);
        responseEntity.setCookie("JSESSIONID", uuid.toString());
        return responseEntity;
    }

    private static Optional<User> findUser(HttpRequest request) {
        Map<String, String> queryStrings = request.getQueryStrings();
        String account = queryStrings.get("account");
        String password = queryStrings.get("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private UUID createSesstion(User user) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid);
        session.setAttribute("user", user);
        SessionManager.add(session);
        SessionManager.add(session);
        return uuid;
    }

    private ResponseEntity loginFailResponse(List<String> headers) {
        headers.add(String.join(" ", "Location: 401.html"));
        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");
    }
}
