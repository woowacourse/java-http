package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        RequestUri requestUri = httpRequest.getRequestUri();
        httpResponse.httpStatus(HttpStatus.OK)
                .body(FileReader.read(requestUri.parseFullPath()), requestUri.findMediaType());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Map<String, String> bodyParams = new HashMap<>();
        String body = httpRequest.getBody();
        for (String element : body.split("&")) {
            String[] split = element.split("=");
            bodyParams.put(split[0], split[1]);
        }
        register(httpResponse, bodyParams);
    }

    private void register(final HttpResponse httpResponse, final Map<String, String> bodyParams) {
        try {
            User user = new User(bodyParams.get("account"), bodyParams.get("password"), bodyParams.get("email"));
            InMemoryUserRepository.save(user);
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .addHeader("Location", "/index.html");
        } catch (Exception e) {
            log.info(e.getMessage());
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .addHeader("Location", "/401.html");
        }
    }
}
