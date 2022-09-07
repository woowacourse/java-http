package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestParameters;
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
        RequestParameters requestParameters = httpRequest.getRequestParameters();
        register(httpResponse, requestParameters);
    }

    private void register(final HttpResponse httpResponse, final RequestParameters requestParameters) {
        try {
            User user = new User(
                    requestParameters.get("account"),
                    requestParameters.get("password"),
                    requestParameters.get("email")
            );
            InMemoryUserRepository.save(user);
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .redirect("/index.html");
        } catch (Exception e) {
            log.info(e.getMessage());
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .redirect("/401.html");
        }
    }
}
