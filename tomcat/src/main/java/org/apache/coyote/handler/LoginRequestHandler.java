package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Response.Http11ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        return httpRequest.getPath().equals("/login")
                && httpRequest.existsBody()
                && Http11Method.POST.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        Http11ResponseBuilder responseBuilder = Http11Response.builder()
                .protocol(httpRequest.getVersionOfProtocol());
        Map<String, String> param = httpRequest.getParsedBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(param.get("account"));
        String redirectPath = "/401.html";
        if (user.isPresent() && user.get().checkPassword(param.get("password"))) {
            redirectPath = "/index.html";
            log.info("로그인 성공 : " + user.get().getAccount());
        }
        return responseBuilder
                .statusCode(302)
                .statusMessage("Found")
                .appendHeader("Location", redirectPath)
                .build();
    }
}
