package nextstep.handler;

import common.FileReader;
import java.io.IOException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler {

    public static final String INDEX_HTML = "/index.html";
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private static final String TEXT_HTML = "text/html;charset=utf-8";

    private Handler() {
    }

    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException {
        final var path = httpRequest.getPath();
        HttpMethod method = httpRequest.getHttpMethod();
        if (method == HttpMethod.GET && path.equals("/")) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.setBody("Hello world!");
            return;
        }
        if (method == HttpMethod.GET && path.isEmpty()) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
            httpResponse.setBody(FileReader.readFile(INDEX_HTML));
            return;
        }
        if (method == HttpMethod.GET && (path.equals("/login") || path.equals("/login.html"))) {
            Session session = httpRequest.getSession();
            if (session != null) {
                httpResponse.setHttpStatus(HttpStatus.FOUND);
                httpResponse.addHeader(HttpHeaders.LOCATION, INDEX_HTML);
                return;
            }
            final var responseBody = FileReader.readFile(httpRequest.getUri());
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
            httpResponse.setBody(responseBody);
            return;
        }
        if (method == HttpMethod.GET && path.endsWith(".html")) {
            final var responseBody = FileReader.readFile(httpRequest.getUri());
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
            httpResponse.setBody(responseBody);
            return;
        }
        if (method == HttpMethod.GET) {
            final var responseBody = FileReader.readFile(httpRequest.getUri());
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE,
                    httpRequest.getHeader(HttpHeaders.ACCEPT));
            httpResponse.setBody(responseBody);
            return;
        }
        if (method == HttpMethod.POST && (path.equals("/login") || path.equals("/login.html"))) {
            login(httpRequest, httpResponse);
            return;
        }
        if (method == HttpMethod.POST && path.equals("/register.html")) {
            register(httpRequest, httpResponse);
        }
    }

    private static void login(HttpRequest httpRequest, HttpResponse httpResponse) {
        final var body = httpRequest.getBody();
        String[] parameters = body.split("&");

        String account = "";
        String password = "";
        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            if (keyValuePair[0].equals("account")) {
                account = keyValuePair[1];
            }
            if (keyValuePair[0].equals("password")) {
                password = keyValuePair[1];
            }
        }

        if (account.isEmpty() || password.isEmpty()) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.LOCATION, "/500.html");
            return;
        }
        checkUser(httpResponse, account, password);
    }

    private static void register(HttpRequest httpRequest, HttpResponse httpResponse) {
        final var body = httpRequest.getBody();
        String[] parameters = body.split("&");

        String account = "";
        String password = "";
        String email = "";
        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            if (keyValuePair[0].equals("account")) {
                account = keyValuePair[1];
            }
            if (keyValuePair[0].equals("password")) {
                password = keyValuePair[1];
            }
            if (keyValuePair[0].equals("email")) {
                email = keyValuePair[1];
            }
        }
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.addHeader(HttpHeaders.LOCATION, "/500.html");
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.LOCATION, INDEX_HTML);
    }

    private static void checkUser(HttpResponse httpResponse, String account, String password) {
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(user -> loginSuccess(httpResponse, user),
                        () -> loginFailed(httpResponse));
    }

    private static void loginSuccess(HttpResponse httpResponse, User user) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
        httpResponse.addHeader(HttpHeaders.LOCATION, INDEX_HTML);
        final var session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addCookie(new HttpCookie(HttpCookie.JSESSIONID, session.getId()));
    }

    private static void loginFailed(HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML);
        httpResponse.addHeader(HttpHeaders.LOCATION, "/401.html");
    }
}
