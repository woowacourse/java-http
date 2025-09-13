package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        handleStaticFileRequest("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.isParams()) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        String account = request.getParams().get("account");
        String password = request.getParams().get("password");
        String email = request.getParams().get("email");

        if (account == null || password == null || email == null) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        User user = new User(InMemoryUserRepository.generateId(), account, password, email);
        InMemoryUserRepository.save(user);

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
