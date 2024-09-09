package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PATH = "static/register.html";

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String requestBody = httpRequest.getBody();
        String[] token = requestBody.split("&");
        if (checkToken(token)) {
            log.error("일부 항목이 누락되었습니다.");
            return new HttpResponse.Builder(new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND))
                    .location("/register")
                    .build();
        }
        String account = token[0].split("=")[1];
        if (InMemoryUserRepository.containsByAccount(account)) {
            log.error("이미 존재하는 account입니다");
            return new HttpResponse.Builder(new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND))
                    .location("/register")
                    .build();
        }
        String email = token[1].split("=")[1];
        String password = token[2].split("=")[1];
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND);

        return new HttpResponse.Builder(httpStatusLine)
                .location("/index.html")
                .build();
    }

    private boolean checkToken(String[] token) {
        return Arrays.stream(token).anyMatch(t -> t.split("=").length < 2);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

            var resourceUrl = getClass().getClassLoader().getResource(REGISTER_PATH);
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
