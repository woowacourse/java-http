package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.UserRequest;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.apache.coyote.http11.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Override
    public ResponseEntity run(final String startLin) throws IOException {
        String path = startLin.split(" ")[1];
        final QueryParam queryParam = new QueryParam(path);

        if (queryParam.matchParameters("account") && queryParam.matchParameters("password")) {

            UserRequest userRequest = new UserRequest(queryParam.getValue("account"),
                    queryParam.getValue("password"));

            final User user = InMemoryUserRepository.findByAccount(userRequest.getAccount())
                    .orElseThrow(UserNotFoundException::new);
            LOGGER.info(user.toString());

            final String response = getContent("/login");
            return new ResponseEntity(StatusCode.OK, path).body(response);
        }
        throw new QueryParamNotFoundException();
    }
}
