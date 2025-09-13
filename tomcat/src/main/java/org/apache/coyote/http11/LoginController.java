package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    
    private final Session session;

    public LoginController(Session session) {
        this.session = session;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasCookies()) {
            User user = (User) session.getStore(request.getCookie().getValue());
            log.info(user.toString());
            response.sendRedirect("/index");
            return;
        }
        
        handleStaticFileRequest("/login.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.isParams()) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        String account = request.getParams().get("account");
        String password = request.getParams().get("password");

        if (account == null || password == null) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            response.sendError(HttpStatus.UNAUTHORIZED);
            return;
        }

        User user = optionalUser.get();
        if (!user.checkPassword(password)) {
            response.sendError(HttpStatus.UNAUTHORIZED);
            return;
        }

        log.info(user.toString());

        HttpCookie cookie = HttpCookie.createSessionId();
        session.addStore(cookie.getValue(), user);

        response.addCookie(cookie);
        response.sendRedirect("/index");
    }
    
    private void handleStaticFileRequest(String path, HttpResponse response) throws IOException {
        if (checkStaticFile(path, response)) {
            return;
        }
        response.sendError(HttpStatus.NOT_FOUND);
    }

    private boolean checkStaticFile(String path, HttpResponse response) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("static" + path);
        if (in == null && !path.contains(".")) {
            in = getClass().getClassLoader().getResourceAsStream("static" + path + ".html");
        }

        if (in == null) {
            return false;
        }

        serveStaticFile(response, in, path);
        return true;
    }

    private void serveStaticFile(HttpResponse response, InputStream inputStream, String path) throws IOException {
        try (inputStream) {
            String ext = "";
            if (path.contains(".")) {
                ext = path.substring(path.lastIndexOf(".") + 1);
            }

            String contentType = MimeTypeResolver.resolve(ext);
            byte[] body = inputStream.readAllBytes();

            response.send(HttpStatus.OK, contentType, body);
        }
    }
}
