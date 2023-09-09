package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;

import static org.apache.coyote.http11.response.StatusCode.OK;

public class MainController implements Controller{

    @Override
    public Response service(RequestReader requestReader) {
        return new Response()
                .addResponseLine(requestReader.getProtocol(), OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByText("Hello world!");
    }
}
