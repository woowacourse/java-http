package org.apache.coyote.http11.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.QueryMapper;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String body = request.getRequestBody().getBody();
        doLoginRequest(new QueryMapper(body), request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException {
        if (request.getRequestHeaders().isExistCookie()) {
            HttpCookie cookie = request.getRequestHeaders().getCookie();
            String jsessionid = cookie.getValues().get("JSESSIONID");
            Session session = SessionManager.findSession(jsessionid);
            if (session != null) {
                response.addStatusLine(HttpStatus.getStatusCodeAndMessage(302));
                response.addContentTypeHeader(ContentType.HTML.getContentType());
                response.addBodyFromFile("/index.html");
                return;
            }
        }
        createLoginPageResponse(request.getPath(), response);
    }

    private void doLoginRequest(QueryMapper queryMapper, HttpRequest request, HttpResponse response) {
        Map<String, String> parameters = queryMapper.getParameters();

        User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(parameters.get("password"))) {
            log.info("user : " + user);
            checkCookieAndReturnResponse(request, response, user);
            return;
        }

        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addBodyFromFile("/401.html");
    }

    private void checkCookieAndReturnResponse(HttpRequest request, HttpResponse response, User user) {
        if (!request.getRequestHeaders().isExistCookie()) {
            UUID uuid = UUID.randomUUID();
            Session session = new Session(uuid.toString());
            session.setAttribute("user", user);
            SessionManager.add(session);
            response.addHeader("Set-Cookie: JSESSIONID=" + uuid);
        }

        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(302));
        response.addBodyFromFile("/index.html");
    }

    private void createLoginPageResponse(String path, HttpResponse response) {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile(path.concat("." + ContentType.HTML.getExtension()));
    }
}
