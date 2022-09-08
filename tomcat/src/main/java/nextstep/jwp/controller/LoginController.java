package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.session.Cookies;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.util.FileReader;

public class LoginController extends AbstractController {

    @Override
    public HttpResponse service(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        Method method = requestLine.getMethod();

        if (method.isPost()) {
            return doPost(request);
        }
        if (method.isGet()) {
            return doGet(request);
        }

        return doNotFoundRequest(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.getValue("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(requestBody.getValue("password"))) {
                UUID uuid = UUID.randomUUID();
                Session session = new Session(uuid.toString());
                session.setAttribute("user", user.get());
                SessionManager.add(session);

                return new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                        .addStatus(HttpStatus.FOUND)
                        .addLocation("/index.html")
                        .addCookie(Cookies.ofJSessionId(session.getId()));
            }
        }
        return new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                .addStatus(HttpStatus.FOUND)
                .addLocation("/401.html");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Optional<String> session = request.getSession();
        if (session.isPresent() && SessionManager.findSession(session.get()).isPresent()) {
            return new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                    .addStatus(HttpStatus.FOUND)
                    .addLocation("/index.html");
        }

        String responseBody = FileReader.readStaticFile("/login.html", this.getClass());
        return new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                .addStatus(HttpStatus.OK)
                .addResponseBody(responseBody, ContentType.TEXT_HTML_CHARSET_UTF_8);
    }
}

