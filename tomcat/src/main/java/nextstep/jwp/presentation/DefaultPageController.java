package nextstep.jwp.presentation;

import nextstep.jwp.exception.NotSupportHttpMethodException;
import org.apache.coyote.http11.http11response.StatusCode;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;
import org.apache.coyote.http11.http11response.ResponseManager;

public class DefaultPageController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        ResponseManager.defaultResponseComponent(response, DEFAULT_MESSAGE, StatusCode.OK);
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        throw new NotSupportHttpMethodException();
    }
}
