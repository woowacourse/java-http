package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;

public class MainPageController implements Controller {

    @Override
    public Response service(RequestReader requestReader) {
        return new Response(requestReader, StatusCode.OK)
                .addBaseHeaders()
                .createBodyByPlainText("Hello world!");
    }
}
