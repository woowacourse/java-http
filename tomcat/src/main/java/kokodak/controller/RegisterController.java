package kokodak.controller;

import java.io.IOException;
import java.util.Map;
import kokodak.RequestMapper;
import kokodak.http.FormDataParser;
import kokodak.http.HttpCookie;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import kokodak.http.Session;
import kokodak.http.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    static {
        RequestMapper.register("/register", new RegisterController());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        final Map<String, String> formData = FormDataParser.parse(httpRequest.getBody());
        final String account = formData.get("account");
        final String password = formData.get("password");
        final String email = formData.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        httpResponse.redirect("http://localhost:8080/index.html");
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        final HttpCookie httpCookie = httpRequest.getHttpCookie();
        final String jSessionId = httpCookie.cookie("JSESSIONID");
        final Session session = SessionManager.findSession(jSessionId);

        if (session == null) {
            doGetRegisterPage(httpRequest, httpResponse);
        } else {
            httpResponse.redirect("http://localhost:8080/index.html");
        }
    }

    private void doGetRegisterPage(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String fileName = "static/register.html";
        httpResponse.setBody(fileName, httpRequest.header("Accept"));
    }
}
