package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.FileAccess;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.content.ContentType;
import nextstep.jwp.http.response.status.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String resource = new FileAccess(request.getPath() + ".html").getFile();

        response.setStatusLine(request.getProtocolVersion(), HttpStatus.OK);
        response.addResponseHeader("Content-Type", ContentType.HTML.getType());
        response.addResponseHeader("Content-Length", String.valueOf(resource.getBytes().length));
        response.setResponseBody(resource);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getAttribute("account");
        String password = request.getAttribute("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(
                        () -> new BadRequestException(HttpStatus.UNAUTHORIZED, "존재하지 않는 아이디입니다."));

        if (!user.checkPassword(password)) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "올바르지 않은 패스워드입니다.");
        }

        response.setStatusLine(request.getProtocolVersion(), HttpStatus.FOUND);
        response.addResponseHeader("Location", "/index.html");
    }
}
