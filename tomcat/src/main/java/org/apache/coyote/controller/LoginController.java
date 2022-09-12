package org.apache.coyote.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.controller.utils.PathFinder;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.request.requestbody.RequestBodyContent;
import org.apache.coyote.http11.httpmessage.response.Response;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        final RequestBodyContent userInput = RequestBodyContent.parse(request.getBody());
        final String account = userInput.getValue("account");
        final String password = userInput.getValue("password");

        if (InMemoryUserRepository.exist(account, password)) {
            final User user = InMemoryUserRepository.findByAccount(account).get();
            log.info("존재하는 유저입니다. ::: " + user);

            if (request.hasHeader("Cookie")) {
                response.redirect("/index.html")
                        .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
                return;
            }
            final Session session = request.getSession(false);
            log.info("새로운 sessionId ::: " + session.getId());
            session.setAttribute("user", user);
            response.redirect("/index.html")
                    .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8")
                    .addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            return;
        }
        log.info("존재하지 않는 유저입니다. ::: " + userInput.getValue("account"));
        response.redirect("/401.html")
                .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        final Path path = PathFinder.findByFileName("/login.html");
        final String responseBody = new String(Files.readAllBytes(path));

        response.ok(responseBody)
                .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
    }
}
