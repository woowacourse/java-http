package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;

public class MainController implements Controller{

    @Override
    public Response service(RequestReader requestReader) {
        return new Response(StatusCode.OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByText("Hello world!");
    }
}
