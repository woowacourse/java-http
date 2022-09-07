package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.RequestBody;
import org.apache.coyote.util.FileUtil;

public class RegisterHandler implements RequestHandler {

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod.isGet()) {
            return doGet(httpRequest);
        }

        if (httpMethod.isPost()) {
            return doPost(httpRequest);
        }
        throw new UnsupportedOperationException();
    }

    private ResponseEntity doGet(HttpRequest httpRequest) {
        String requestUri = httpRequest.getRequestUri();
        String body = FileUtil.readAllBytes(requestUri + ".html");
        return new ResponseEntity(HttpStatus.OK, body, ContentType.HTML);
    }

    private ResponseEntity doPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        User user = new User(requestBody.value("account"), requestBody.value("password"), requestBody.value("email"));
        InMemoryUserRepository.save(user);
        return new ResponseEntity(HttpStatus.FOUND, ContentType.HTML, new HttpHeaders(Map.of("Location", "/index.html")));
    }
}
