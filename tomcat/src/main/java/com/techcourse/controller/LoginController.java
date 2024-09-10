package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.getMethod().equals(HttpMethod.GET)) {
            try {
                File requestFile = ResourceParser.getRequestFile(req);
                resp.setResponse("200 OK", requestFile);
            } catch (IOException e) {
                log.warn("파일 읽기/쓰기 과정에서 예외 발생 (Path: %s)".formatted(req.getPath()));
            }

            Map<String, String> requestQueries = req.getQueries();

            String account = "";
            String password = "";
            for (Entry<String, String> entry : requestQueries.entrySet()) {
                if (entry.getKey().equals(ACCOUNT)) {
                    account = entry.getValue();
                } else if (entry.getKey().equals(PASSWORD)) {
                    password = entry.getValue();
                }
            }

            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("찾으시는 유저가 존재하지 않습니다."));
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("찾으시는 유저가 존재하지 않습니다.");
            }

            log.info(user.toString());

        } else {
            throw new IllegalArgumentException("%s 요청을 처리할 컨트롤러가 존재하지 않습니다.".formatted(req.getMethod().getValue()));
        }
    }
}
