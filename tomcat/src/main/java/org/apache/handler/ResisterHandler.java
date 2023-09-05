package org.apache.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.common.ContentType;
import org.apache.common.FileReader;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResisterHandler implements RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResisterHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }

        if (httpRequest.isGet()) {
            return doGet();
        }

        throw new IllegalArgumentException("일치하는 Method 타입이 없습니다.");
    }

    public HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        String query = httpRequest.getBody();
        String[] queries = query.split("&");
        String account = queries[0].split("=")[1];
        String email = queries[1].split("=")[1];
        String password = queries[2].split("=")[1];

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
        LOG.info("회원가입 성공한 회원 : {}", user);
        String content = FileReader.read("/index.html");
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, content);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        return httpResponse;
    }

    public HttpResponse doGet() throws IOException {
        String content = FileReader.read("/register.html");
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, content);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        return httpResponse;
    }
}
