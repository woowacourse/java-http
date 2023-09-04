package org.apache.handler;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.common.ContentType;
import org.apache.common.FileReader;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }

        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }

        throw new IllegalArgumentException("일치하는 Method 타입이 없습니다.");
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        String query = httpRequest.getBody();
        String[] queries = query.split("&");
        String account = queries[0].split("=")[1];
        String password = queries[1].split("=")[1];

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            LOG.info("user : {}", user);
            String content = FileReader.read("/index.html");
            return new HttpResponse(HttpStatus.FOUND, ContentType.TEXT_HTML, content);
        }

        String content = FileReader.read("/401.html");
        return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.TEXT_HTML, content);
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        String content = FileReader.read("/login.html");
        return new HttpResponse(HttpStatus.OK, ContentType.TEXT_HTML, content);
    }
}
