package nextstep.jwp.model.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.http_response.JwpHttpResponse;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegisterHandler implements CustomHandler {

    private static final String RESOURCE_PREFIX = "static/";
    private static final String REGISTER_PAGE_PATH = "register.html";
    private static final String REGISTER_SUCCESS_PATH = "index.html";

    @Override
    public void handle(JwpHttpRequest jwpHttpRequest, OutputStream outputStream) throws IOException, URISyntaxException {
        if (jwpHttpRequest.isEmptyParams()) {
            String resourceUri = RESOURCE_PREFIX + REGISTER_PAGE_PATH;
            String resourceFile = findResourceFile(resourceUri);
            final String response = JwpHttpResponse.ok(resourceUri, resourceFile);
            outputStream.write(response.getBytes());
            return;
        }

        User user = assembleUser(jwpHttpRequest);
        InMemoryUserRepository.save(user);

        String response = JwpHttpResponse.found(REGISTER_SUCCESS_PATH);
        outputStream.write(response.getBytes());
    }

    private User assembleUser(JwpHttpRequest jwpHttpRequest) {
        String account = jwpHttpRequest.getParam("account");
        String password = jwpHttpRequest.getParam("password");
        String email = jwpHttpRequest.getParam("email");
        return new User(account, password, email);
    }

    private String findResourceFile(String resourceUri) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(resourceUri);
        final Path path = Paths.get(resource.toURI());
        return new String(Files.readAllBytes(path));
    }

}
