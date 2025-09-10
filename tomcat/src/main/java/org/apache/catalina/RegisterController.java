package org.apache.catalina;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.ServletException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterController extends AbstractController {

    private static final String STATIC_FILE_LOCATION = "static";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        super.service(request, response);
    }

    @Override
    void doGet(Http11Request request, Http11Response response) throws Exception {
        final byte[] fileContent = readFile("/register.html");

        response.setStaticResponse(HttpStatus.OK, fileContent, HTML_CONTENT_TYPE);
    }

    @Override
    void doPost(Http11Request request, Http11Response response) throws Exception {
        final Map<String, String> urlEncodedResponseBody = request.getBodyByContentType("application/x-www-form-urlencoded");

        final String account = urlEncodedResponseBody.get("account");
        final String email = urlEncodedResponseBody.get("email");
        final String password = urlEncodedResponseBody.get("password");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException(String.format("Already signed up : account = %s", account));
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final Session session = handleAuthorizedRequest(user);

        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=Strict", session.getId()));

        response.setRedirectResponse("/index.html", headers);
    }

    private byte[] readFile(final String location) throws IOException {
        try (final InputStream fileInputStream = new FileInputStream(getClass().getClassLoader().getResource(STATIC_FILE_LOCATION + location).getPath())) {
            return fileInputStream.readAllBytes();
        } catch (final NullPointerException e) {
            throw new NoSuchFileException(location);
        }
    }

    private Session handleAuthorizedRequest(final User user) {
        final String sessionId = generateSessionID();

        final Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return session;
    }

    private String generateSessionID() {
        final UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
