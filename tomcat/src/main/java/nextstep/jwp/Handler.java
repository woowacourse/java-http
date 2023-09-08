package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestURI;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;

public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private static final SessionManager sessionManager = new SessionManager();

    private Handler() {
    }

    public static HttpResponse run(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestLine().getRequestURI();
        if (requestURI.isLoginPage() && httpRequest.isMethod(GET)) {
            ResponseBody responseBody = new ResponseBody(parseResponseBody(requestURI.getResourcePath()));
            return HttpResponse.builder()
                    .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.OK))
                    .contentType(httpRequest.contentType())
                    .contentLength(responseBody.getValue().length())
                    .responseBody(responseBody)
                    .build();
        }
        if (requestURI.isLoginPage() && httpRequest.isMethod(POST)) {
            return doLogin(httpRequest);
        }
        if (requestURI.isHome()) {
            return HttpResponse.builder()
                    .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.OK))
                    .contentType(httpRequest.contentType())
                    .contentLength("Hello world!".getBytes().length)
                    .responseBody(new ResponseBody("Hello world!"))
                    .build();
        }
        if (requestURI.isRegister()) {
            return doRegister(httpRequest);
        }
        ResponseBody responseBody = new ResponseBody(parseResponseBody(requestURI.getResourcePath()));
        return HttpResponse.builder()
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.OK))
                .contentType(httpRequest.contentType())
                .contentLength(responseBody.getValue().length())
                .responseBody(responseBody)
                .build();
    }

    private static HttpResponse doRegister(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRequestBodyEmpty()) {
            String resourcePath = httpRequest.getRequestLine().getRequestURI().getResourcePath();
            ResponseBody responseBody = new ResponseBody(parseResponseBody(resourcePath));
            return HttpResponse.builder()
                    .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND))
                    .contentType(httpRequest.contentType())
                    .contentLength(responseBody.getValue().length())
                    .redirect("http://localhost:8080/register.html")
                    .responseBody(responseBody)
                    .build();
        }

        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValueOf("account");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            String resourcePath = httpRequest.getRequestLine().getRequestURI().getResourcePath();
            ResponseBody responseBody = new ResponseBody(parseResponseBody(resourcePath));
            return HttpResponse.builder()
                    .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND))
                    .contentType(httpRequest.contentType())
                    .contentLength(responseBody.getValue().length())
                    .redirect("http://localhost:8080/register.html")
                    .responseBody(responseBody)
                    .build();
        }
        String password = requestBody.getValueOf("password");
        String email = requestBody.getValueOf("email");
        InMemoryUserRepository.save(new User(account, password, email));
        String resourcePath = httpRequest.getRequestLine().getRequestURI().getResourcePath();
        ResponseBody responseBody = new ResponseBody(parseResponseBody(resourcePath));
        return HttpResponse.builder()
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.CREATED))
                .contentType(httpRequest.contentType())
                .contentLength(responseBody.getValue().length())
                .redirect("http://localhost:8080/login.html")
                .responseBody(responseBody)
                .build();
    }

    private static HttpResponse doLogin(HttpRequest httpRequest) throws IOException {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValueOf("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        log.info(user.toString());
        String password = requestBody.getValueOf("password");
        if (!user.checkPassword(password)) {
            String resourcePath = httpRequest.getRequestLine().getRequestURI().getResourcePath();
            ResponseBody responseBody = new ResponseBody(parseResponseBody(resourcePath));
            return HttpResponse.builder()
                    .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.UNAUTHORIZED))
                    .contentType(httpRequest.contentType())
                    .contentLength(responseBody.getValue().length())
                    .redirect("http://localhost:8080/401")
                    .responseBody(responseBody)
                    .build();
        }
        HttpCookie cookie = HttpCookie.from(httpRequest.getHeaderValue("Cookie"));
        Session foundSession = sessionManager.findSession(cookie.getValue("JSESSIONID"));
        if (foundSession != null) {
            String resourcePath = httpRequest.getRequestLine().getRequestURI().getResourcePath();
            ResponseBody responseBody = new ResponseBody(parseResponseBody(resourcePath));
            return HttpResponse.builder()
                    .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND))
                    .contentType(httpRequest.contentType())
                    .contentLength(responseBody.getValue().length())
                    .redirect("http://localhost:8080/index")
                    .setCookie(cookie)
                    .responseBody(responseBody)
                    .build();
        }
        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        session.setAttribute("user", user);
        sessionManager.add(session);
        String resourcePath = httpRequest.getRequestLine().getRequestURI().getResourcePath();
        ResponseBody responseBody = new ResponseBody(parseResponseBody(resourcePath));
        return HttpResponse.builder()
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND))
                .contentType(httpRequest.contentType())
                .contentLength(responseBody.getValue().length())
                .redirect("http://localhost:8080/index")
                .responseBody(responseBody)
                .build();
    }

    private static String parseResponseBody(String resourcePath) throws IOException {
        Path path = new File(Objects.requireNonNull(
                Handler.class.getClassLoader().getResource(resourcePath)).getFile()
        ).toPath();
        return new String(Files.readAllBytes(path));
    }

}
