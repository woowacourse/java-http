package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.PathResponse;
import org.apache.coyote.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginController implements Controller{

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    public static final String QUERY_ACCOUNT_KEY = "account";
    public static final String QUERY_PASSWORD_KEY = "password";

    @Override
    public Response handle(final Request request){
        Map<String, String> queryMap = request.getQueryParaMap();

        if(!queryMap.containsKey(QUERY_ACCOUNT_KEY) || !queryMap.containsKey(QUERY_PASSWORD_KEY)){
            throw new IllegalArgumentException();
        }

        final String account = queryMap.get(QUERY_ACCOUNT_KEY);
        final String password = queryMap.get(QUERY_PASSWORD_KEY);

        User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if(user.checkPassword(password)){
            log.info("user : {}", user);
        }

        return new PathResponse(request.getPath());
    }
}
