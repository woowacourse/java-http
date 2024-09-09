package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;

public class RegisterController extends AbstractController {

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
        if (InMemoryUserRepository.containsByAccount(account)) {
            throw new RuntimeException("이미 존재하는 account입니다");
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

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

            String fileName = "static/register.html";
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
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
