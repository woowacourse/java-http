package nextstep.jwp.controller;

import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.FileReader;

import static org.apache.coyote.http11.request.HttpMethod.GET;

public class LoginController implements Controller {

    private static final String URI = "/login.html";

    private static final SessionManager sessionManager = new SessionManager();


    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isMethod(GET)) {
            ResponseBody responseBody = new ResponseBody(FileReader.read(URI));
            StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
            return HttpResponse.builder()
                    .contentType(ContentType.HTML.getValue())
                    .contentLength(responseBody.getValue().length())
                    .statusLine(statusLine)
                    .responseBody(responseBody)
                    .build();
        } else {
            return doLogin(request);
        }
    }

    private static HttpResponse doLogin(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValueOf("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        String password = requestBody.getValueOf("password");
        if (!user.checkPassword(password)) {
            ResponseBody responseBody = new ResponseBody(FileReader.read(URI));
            StatusLine statusLine = new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.UNAUTHORIZED);
            return HttpResponse.builder()
                    .statusLine(statusLine)
                    .contentType(ContentType.HTML.getValue())
                    .contentLength(responseBody.getValue().getBytes().length)
                    .redirect("http://localhost:8080/401")
                    .responseBody(responseBody)
                    .build();
        }
        HttpCookie cookie = HttpCookie.from(httpRequest.getHeaderValue("Cookie"));
        Session foundSession = sessionManager.findSession(cookie.getValue("JSESSIONID"));
        StatusLine statusLine = new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND);
        if (foundSession != null) {
            ResponseBody responseBody = new ResponseBody(FileReader.read("/index.html"));
            return HttpResponse.builder()
                    .statusLine(statusLine)
                    .contentType(ContentType.HTML.getValue())
                    .contentLength(responseBody.getValue().getBytes().length)
                    .redirect("http://localhost:8080/index")
                    .setCookie(cookie)
                    .responseBody(responseBody)
                    .build();
        }
        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        session.setAttribute("user", user);
        sessionManager.add(session);
        ResponseBody responseBody = new ResponseBody(FileReader.read("/index.html"));
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect("http://localhost:8080/index")
                .responseBody(responseBody)
                .build();
    }

}
