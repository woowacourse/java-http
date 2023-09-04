package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.PathResponse;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StaticResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.Map;

public class LoginController implements Controller{

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    public static final String QUERY_ACCOUNT_KEY = "account";
    public static final String QUERY_PASSWORD_KEY = "password";

    @Override
    public Response handle(final Request request){
        if(request.isPost()){
            return login(request);
        }
        return new StaticResponse("html", "/login.html", 200, "OK");
    }

    private static PathResponse login(Request request) {
        Map<String, String> requestBody = request.getRequestBody();

        if(!requestBody.containsKey(QUERY_ACCOUNT_KEY) || !requestBody.containsKey(QUERY_PASSWORD_KEY)){
            return new PathResponse("/401", HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
        }

        final String account = requestBody.get(QUERY_ACCOUNT_KEY);
        final String password = requestBody.get(QUERY_PASSWORD_KEY);

        User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if(user.checkPassword(password)){
            log.info("user : {}", user);
            return new PathResponse("/index", HttpURLConnection.HTTP_MOVED_TEMP, "Temporary Redirect");
        }

        return new PathResponse("/401", HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
    }
}
