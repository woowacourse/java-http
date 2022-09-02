package nextstep.jwp.handler;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryStrings;
import org.apache.coyote.http11.enums.FilePath;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public HttpResponse login(HttpRequest httpRequest) {
        QueryStrings queryStrings = httpRequest.getQueryStrings();

        String account = queryStrings.findByKey(ACCOUNT);
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));

        String password = queryStrings.findByKey(PASSWORD);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return generateResponse(HttpStatus.FOUND, httpRequest, FilePath.INDEX_PAGE);
        }

        return generateResponse(HttpStatus.UNAUTHORIZED, httpRequest, FilePath.ERROR_401_PAGE);
    }

    private HttpResponse generateResponse(HttpStatus httpStatus, HttpRequest httpRequest, FilePath filePath) {
        String responseBody = filePath.generateFile();
        return new HttpResponse(httpStatus, httpRequest, responseBody);
    }
}
