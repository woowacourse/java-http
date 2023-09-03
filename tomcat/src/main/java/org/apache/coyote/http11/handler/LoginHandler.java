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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        if (request.getRequestMethod() == RequestMethod.GET) {
            Cookie cookie = request.parseCookie();
            String jsessionid = cookie.findByKey("JSESSIONID");

            if (isLoginUser(jsessionid)) {
                return ResponseEntity.redirect("index.html");
            }

            String fileData = readLoginFile();
            List<String> headers = List.of(
                    String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                    String.join(" ", "Content-Length:", String.valueOf(fileData.getBytes().length))
            );

            return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, fileData);

        }
        if (request.getRequestMethod() == RequestMethod.POST) {
            Optional<User> userResult = findUser(request);
            if (userResult.isPresent()) {
                return loginSuccessResponse(userResult);
            }
            return loginFailResponse();
        }
        throw new UnsupportedOperationException("get, post만 가능합니다.");
    }

    private String readLoginFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("static/login.html");
        File file = new File(resource.getFile());
        String fileData = new String(Files.readAllBytes(file.toPath()));
        return fileData;
    }

    private boolean isLoginUser(String jsessionid) throws IOException {
        if (jsessionid == null) {
            return false;
        }
        Session session = SessionManager.findSession(UUID.fromString(jsessionid));
        return session != null;
    }

    private ResponseEntity loginSuccessResponse(Optional<User> userResult) {
        User user = userResult.get();
        ResponseEntity responseEntity = ResponseEntity.redirect("index.html");
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

    private ResponseEntity loginFailResponse() {
        return ResponseEntity.redirect("401.html");
    }
}
