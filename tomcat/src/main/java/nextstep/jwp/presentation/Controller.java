package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final String HTML_SUFFIX = ".html";
    private static final List<String> STATIC_PATH = List.of(".css", ".js", ".ico", HTML_SUFFIX);
    private static final String MULTIPLE_QUERY_STRING_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String SESSION_ID = "JSESSIONID";

    private HttpResponseBuilder httpResponseBuilder;

    public Controller() {
        this.httpResponseBuilder = new HttpResponseBuilder();
    }

    public String handle(HttpRequestParser httpRequestParser) throws IOException {
        String method = httpRequestParser.findMethod();

        if (method.equals("GET")) {
            return handleGet(httpRequestParser);
        }

        if (method.equals("POST")) {
            return handlePost(httpRequestParser);
        }

        throw new UnsupportedOperationException("지원하지 않는 HTTP Method 입니다.");
    }

    private String handleGet(HttpRequestParser httpRequestParser) throws IOException {
        String path = httpRequestParser.findPathWithoutQueryString();

        if (isStaticPath(path)) {
            return httpResponseBuilder.buildStaticFileOkResponse(httpRequestParser, path);
        }

        if (path.equals("/")) {
            return httpResponseBuilder.buildCustomResponse(httpRequestParser, "Hello world!");
        }

        if (path.equals("/login")) {
            Map<String, String> cookies = httpRequestParser.findCookies();
            if (cookies.containsKey(SESSION_ID) && SessionManager.isAlreadyLogin(cookies.get(SESSION_ID))) {
                return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/index.html");
            }
            processQueryString(httpRequestParser);
            return httpResponseBuilder.buildStaticFileOkResponse(httpRequestParser, "/login.html");
        }

        return httpResponseBuilder.buildStaticFileOkResponse(httpRequestParser, path + HTML_SUFFIX);
    }

    private void processQueryString(HttpRequestParser httpRequestParser) {
        Map<String, String> queryStrings = httpRequestParser.findQueryStrings();
        if (queryStrings.containsKey("account") && queryStrings.containsKey("password")) {
            String account = queryStrings.get("account");
            String password = queryStrings.get("password");
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
            if (user.checkPassword(password)) {
                log.info(user.toString());
            }
        }
    }

    private boolean isStaticPath(String path) {
        return STATIC_PATH.stream().anyMatch(path::endsWith);
    }

    private String handlePost(HttpRequestParser httpRequestParser) throws IOException {
        String path = httpRequestParser.findPathWithoutQueryString();

        if (path.equals("/login")) {
            String[] splitRequestBody = httpRequestParser.getMessageBody().split(MULTIPLE_QUERY_STRING_SEPARATOR);
            String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
            String password = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
            try {
                User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
                addSession(user, httpRequestParser);
                return getRedirectPath(password, user, httpRequestParser);
            } catch (UserNotFoundException e) {
                return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/401.html");
            }
        }

        if (path.equals("/register")) {
            String[] splitRequestBody = httpRequestParser.getMessageBody().split(MULTIPLE_QUERY_STRING_SEPARATOR);
            String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
            String email = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
            email = email.replace("%40", "@");
            String password = splitRequestBody[2].split(KEY_VALUE_SEPARATOR)[1];

            InMemoryUserRepository.save(new User(account, password, email));
            return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/index.html");
        }

        throw new UnsupportedOperationException("지원하지 않는 Method 입니다.");
    }

    private void addSession(User user, HttpRequestParser httpRequestParser) {
        Map<String, String> cookies = httpRequestParser.findCookies();
        if (!cookies.containsKey(SESSION_ID)) {
            String uuid = UUID.randomUUID().toString();
            addCookie(httpRequestParser, cookies, uuid);
            cookies.put(SESSION_ID, uuid);
        }
        String jsessionid = cookies.get(SESSION_ID);
        SessionManager.add(jsessionid, user);
    }

    private void addCookie(HttpRequestParser httpRequestParser, Map<String, String> cookies, String uuid) {
        if (cookies.isEmpty()) {
            httpRequestParser.addHeader("Cookie", SESSION_ID + "=" + uuid);
            return;
        }
        String existedCookie = cookies.get(SESSION_ID);
        httpRequestParser.addHeader("Cookie", existedCookie + "; " + SESSION_ID + "=" + uuid);
    }

    private String getRedirectPath(String password, User user, HttpRequestParser httpRequestParser) throws IOException {
        if (user.checkPassword(password)) {
              return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/index.html");
        }
        return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/401.html");
    }
}
