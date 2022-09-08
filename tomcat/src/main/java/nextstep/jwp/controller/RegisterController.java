package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.request.RequestParameters;
import org.apache.coyote.http11.request.RequestUri;
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
            httpResponse.redirect("/index.html");
        } catch (Exception e) {
            log.info(e.getMessage());
            httpResponse.redirect("/401.html");
        }
    }
}
