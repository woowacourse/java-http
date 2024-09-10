package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import org.apache.coyote.http11.httprequest.HttpCookie;
import org.apache.coyote.http11.httprequest.HttpCookieConvertor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PATH = "static/login.html";

    private final Session session = Session.getInstance();

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String requestBody = httpRequest.getBody();
        String[] token = requestBody.split("&");
        if (checkToken(token)) {
            log.error("일부 항목이 누락되었습니다.");
            return new HttpResponse.Builder(new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND))
                    .location("/login")
                    .build();
        }
        String account = token[0].split("=")[1];
        String password = token[1].split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        UUID uuid = UUID.randomUUID();

        HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND);
        if (user.checkPassword(password)) {
            session.save(uuid.toString(), user);
            log.info(user.toString());
            return new HttpResponse.Builder(httpStatusLine)
                    .setCookie("JSESSIONID=" + uuid)
                    .location("/index.html")
                    .build();
        }
        log.error("비밀번호 불일치");
        return new HttpResponse.Builder(httpStatusLine)
                .location("/401.html")
                .build();
    }

    private boolean checkToken(String[] token) {
        return Arrays.stream(token).anyMatch(t -> t.split("=").length < 2);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            if (!httpRequest.containsKey("Cookie")) {
                return redirectLoginPage(httpRequest);
            }

            HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(httpRequest.getValue("Cookie"));
            if (!httpCookie.containsKey("JSESSIONID")) {
                return redirectLoginPage(httpRequest);
            }

            String jsessionid = httpCookie.getValue("JSESSIONID");
            if (!session.containsUser(jsessionid)) {
                return redirectLoginPage(httpRequest);
            }

            User user = session.getUser(jsessionid);
            log.info(user.toString());
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND);
            return new HttpResponse.Builder(httpStatusLine)
                    .setCookie("JSESSIONID=" + jsessionid)
                    .location("/index.html")
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse redirectLoginPage(HttpRequest httpRequest) throws URISyntaxException, IOException {
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

        var resourceUrl = getClass().getClassLoader().getResource(LOGIN_PATH);
        Path filePath = Path.of(resourceUrl.toURI());
        String responseBody = new String(Files.readAllBytes(filePath));

        return new HttpResponse.Builder(httpStatusLine)
                .contentType(Files.probeContentType(filePath) + ";charset=utf-8")
                .contentLength(String.valueOf(responseBody.getBytes().length))
                .responseBody(responseBody)
                .build();
    }
}
