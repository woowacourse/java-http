package nextstep.jwp.model.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginHandler implements CustomHandler {

    private static final String RESOURCE_PREFIX = "static";
    private static final String LOGIN_PAGE_PATH = "/login.html";

    @Override
    public void handle(JwpHttpRequest jwpHttpRequest, OutputStream outputStream) throws IOException, URISyntaxException {
        if (jwpHttpRequest.isEmptyParams()) {
            String resourceFile = findResourceFile(RESOURCE_PREFIX + LOGIN_PAGE_PATH);
            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + resourceFile.getBytes().length + " ",
                    "",
                    resourceFile);
            outputStream.write(response.getBytes());
            return;
        }
        // todo: response는 상수록 묶기 JwpHttpResponse 클래스로 묶으면 될듯!

        String account = jwpHttpRequest.getParam("account");
        String password = jwpHttpRequest.getParam("password");
        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(user -> requestLogin(user, password, outputStream),
                        () -> loginFail(outputStream));
    }

    private void requestLogin(User user, String password, OutputStream outputStream) {
        if (user.checkPassword(password)) {
            String redirectUri = "index.html";
            final String response = String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: http://localhost:8080/" + redirectUri + " ",
                    "",
                    "");
            try {
                outputStream.write(response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        loginFail(outputStream);
    }

    private void loginFail(OutputStream outputStream) {
        String redirectUri = "401.html";
        final String response = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: http://localhost:8080/" + redirectUri + " ",
                "",
                "");
        try {
            outputStream.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String findResourceFile(String resourceUrl) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(resourceUrl);
        final Path path = Paths.get(resource.toURI());
        String resourceFile = new String(Files.readAllBytes(path));
        return resourceFile;
    }
}
