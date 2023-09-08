package nextstep.jwp.controller.page;

import static nextstep.jwp.controller.FileContent.HTML;
import static nextstep.jwp.controller.FileContent.INDEX_URI;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.PathUtil;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String BODY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final int VALUE_INDEX = 1;

    private RegisterController() {
    }

    public static Controller create() {
        return new RegisterController();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final Path path = PathUtil.findPathWithExtension(uri, HTML);
        return HttpResponse.create(HttpStatus.OK, path);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {

        final Path path = PathUtil.findPathWithExtension(INDEX_URI, HTML);
        final String requestBody = request.getRequestBody();
        saveUser(requestBody);

        return HttpResponse.createRedirect(HttpStatus.FOUND, path, INDEX_URI + HTML);
    }

    private void saveUser(final String requestBody) {
        final String[] splitUserInfo = requestBody.split(BODY_DELIMITER);
        validateParameterLength(splitUserInfo);

        final String account = splitUserInfo[0].split(PARAM_DELIMITER)[VALUE_INDEX];
        final String password = splitUserInfo[1].split(PARAM_DELIMITER)[VALUE_INDEX];
        final String email = splitUserInfo[2].split(PARAM_DELIMITER)[VALUE_INDEX];
        validateExistUserInMemory(account);

        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
    }

    private void validateExistUserInMemory(final String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }

    private void validateParameterLength(final String[] splitUserInfo) {
        if (splitUserInfo.length != 3) {
            throw new IllegalArgumentException("아이디, 이메일, 비밀번호가 전부 들어와야 합니다.");
        }
    }
}
