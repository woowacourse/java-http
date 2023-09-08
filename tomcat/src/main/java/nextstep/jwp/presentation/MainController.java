package nextstep.jwp.presentation;

import org.apache.coyote.http11.RequestReader;
import org.apache.coyote.http11.Response;
import org.apache.coyote.http11.StatusCode;

public class MainController implements Controller{

    @Override
    public Response service(RequestReader requestReader) {
        return new Response(StatusCode.OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByText("Hello world!");
    }
}
