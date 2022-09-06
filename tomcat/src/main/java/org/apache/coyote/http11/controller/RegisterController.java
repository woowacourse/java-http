package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.response.StatusCode.*;

import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.FileReader;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {
    private static final FileReader fileReader = new FileReader();

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    private static final String INDEX_PATH = "/index.html";
    private static final String REGISTER_PATH = "/register.html";
    private static final String JSESSIONID = "JSESSIONID";

    public RegisterController() {
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (SessionManager.contains(request.getCookie(JSESSIONID))) {
            return redirect();
        }
        return fileReader.readFile(REGISTER_PATH, HTTP_VERSION_1_1);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();

        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        return redirect();
    }

    private HttpResponse redirect() {
        return HttpResponse.of(HTTP_VERSION_1_1, FOUND)
            .addHeader("Location", INDEX_PATH);
    }
}
