package nextstep.jwp.controller.page;

import static nextstep.jwp.controller.FileContent.HTML;
import static nextstep.jwp.controller.FileContent.INDEX_URI;
import static nextstep.jwp.controller.FileContent.STATIC;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatusLine;

public class RegisterGetPageController extends AbstractController {

    private static final String BODY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final int VALUE_INDEX = 1;

    private RegisterGetPageController() {
    }

    public static Controller create() {
        return new RegisterGetPageController();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + uri + HTML);

        final Path path = new File(url.getPath()).toPath();

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        final String responseBody = new String(content);

        return new HttpResponse(ResponseStatusLine.create(HttpStatus.OK), headers, responseBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + INDEX_URI + HTML);
        final Path path = new File(url.getPath()).toPath();

        final String[] splitUserInfo = request.getRequestBody().split(BODY_DELIMITER);
        if (splitUserInfo.length != 3) {
            throw new IllegalArgumentException("아이디, 이메일, 비밀번호가 전부 들어와야 합니다.");
        }

        final String account = splitUserInfo[0].split(PARAM_DELIMITER)[VALUE_INDEX];
        final String password = splitUserInfo[1].split(PARAM_DELIMITER)[VALUE_INDEX];
        final String email = splitUserInfo[2].split(PARAM_DELIMITER)[VALUE_INDEX];
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        headers.setHeader(HttpHeaders.LOCATION, INDEX_URI + HTML);

        final String responseBody = new String(content);

        return new HttpResponse(ResponseStatusLine.create(HttpStatus.FOUND), headers, responseBody);
    }
}
