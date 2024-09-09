package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpCookieConvertor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpResponse.Builder;
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
        for (String t : token) {
            if (t.split("=").length < 2) {
                throw new RuntimeException("변수가 부족합니다");
            }
        }
        String account = token[0].split("=")[1];
        String password = token[1].split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        UUID uuid = UUID.randomUUID();

        HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND);
        Builder builder = new Builder(httpStatusLine);
        if (user.checkPassword(password)) {
            session.save(uuid.toString(), user);
            builder.setCookie("JSESSIONID=" + uuid);
            builder.location("/index.html");
            log.info(user.toString());
        } else {
            builder.location("/401.html");
            log.error("비밀번호 불일치");
        }

        return builder.build();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

            if (httpRequest.containsKey("Cookie")) {
                HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(httpRequest.getValue("Cookie"));
                if (httpCookie.containsKey("JSESSIONID")) {
                    String jsessionid = httpCookie.getValue("JSESSIONID");
                    if (session.containsUser(jsessionid)) {
                        User user = session.getUser(jsessionid);
                        log.info(user.toString());
                        httpStatusLine = new HttpStatusLine(httpStatusLine.getVersion(), HttpStatusCode.FOUND);
                        return new HttpResponse.Builder(httpStatusLine)
                                .setCookie("JSESSIONID=" + jsessionid)
                                .location("/index.html")
                                .build();
                    }
                }
            }

            var resourceUrl = getClass().getClassLoader().getResource(LOGIN_PATH);
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));

            return new HttpResponse.Builder(httpStatusLine)
                    .contentType(Files.probeContentType(filePath) + ";charset=utf-8")
                    .contentLength(String.valueOf(responseBody.getBytes().length))
                    .responseBody(responseBody)
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
