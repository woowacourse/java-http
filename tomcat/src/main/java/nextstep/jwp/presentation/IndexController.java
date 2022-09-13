package nextstep.jwp.presentation;

import nextstep.jwp.exception.NotSupportHttpMethodException;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;
import org.apache.coyote.http11.http11response.ResponseManager;

public class IndexController extends AbstractController {

    private static final String REDIRECT_URI = "/index.html";

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        throw new NotSupportHttpMethodException();
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        ResponseManager.redirectResponseComponent(response, REDIRECT_URI, StatusCode.OK);
    }
}
