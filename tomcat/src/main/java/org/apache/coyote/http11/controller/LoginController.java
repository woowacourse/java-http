package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpResponseBody;
import org.apache.coyote.http11.httpresponse.HttpResponseHeader;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

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
        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        if (user.checkPassword(password)) {
            session.save(uuid.toString(), user);
            httpResponseHeader.addHeaders("Set-Cookie", "JSESSIONID=" + uuid);
            httpResponseHeader.addHeaders("Location", "/index.html");
            log.info(user.toString());
        } else {
            httpResponseHeader.addHeaders("Location", "/401.html");
            log.error("비밀번호 불일치");
        }

        return new HttpResponse(httpStatusLine, httpResponseHeader);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

            if (httpRequest.containsKey("Cookie")) {
                String[] cookies = httpRequest.getValue("Cookie").split("; ");
                String cookie = "";
                for (String c : cookies) {
                    if (c.contains("JSESSIONID") && c.split("=").length >= 2) {
                        cookie = c.split("=")[1];
                    }
                }
                if (session.containsUser(cookie)) {
                    User user = session.getUser(cookie);
                    log.info(user.toString());
                    httpStatusLine = new HttpStatusLine(httpStatusLine.getVersion(), HttpStatusCode.FOUND);
                    HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
                    httpResponseHeader.addHeaders("Set-Cookie", "JSESSIONID=" + cookie);
                    httpResponseHeader.addHeaders("Location", "/index.html");
                    return new HttpResponse(httpStatusLine, httpResponseHeader);
                }
            }

            String fileName = "static/login.html";
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));
            HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
            httpResponseHeader.addHeaders("Content-Type", Files.probeContentType(filePath) + ";charset=utf-8");
            httpResponseHeader.addHeaders("Content-Length", String.valueOf(responseBody.getBytes().length));
            HttpResponseBody httpResponseBody = new HttpResponseBody(responseBody);

            return new HttpResponse(httpStatusLine, httpResponseHeader, httpResponseBody);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
