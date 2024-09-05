package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Response.Http11ResponseBuilder;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return httpRequest.getPath().equals("/login")
                && httpRequest.isExistsBody()
                && Http11Method.POST.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> param = httpRequest.getParsedBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(param.get("account"));
        String redirectPath = "/401.html";
        if (user.isPresent() && user.get().checkPassword(param.get("password"))) {
            redirectPath = "/index.html";
            log.info("로그인 성공 : " + user.get().getAccount());
        }
        Http11ResponseBuilder responseBuilder = Http11Response.builder();
        if (httpRequest.isNotExistsCookie("JSESSIONID")) {
            responseBuilder.appendHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        }
        return responseBuilder
                .status(HttpStatus.FOUND)
                .appendHeader("Location", redirectPath)
                .build();
    }
}
