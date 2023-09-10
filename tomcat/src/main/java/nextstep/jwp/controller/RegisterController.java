package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpBody;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.FormData;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_PATH = "static/register.html";
    private static final String INDEX_URI = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        join(request.getHttpBody());

        response.setStatus(HttpStatus.FOUND);
        response.setLocation(INDEX_URI);
    }

    private void join(HttpBody httpBody) {
        FormData formData = FormData.from(httpBody);

        String account = formData.get(ACCOUNT);
        String password = formData.get(PASSWORD);
        String email = formData.get(EMAIL);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);

        response.setContentType(ContentType.extractValueFromPath(request.getNativePath()));
        response.setBody(new String(Files.readAllBytes(Path.of(url.getPath()))));
    }

}
