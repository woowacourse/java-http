package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;

import java.io.IOException;

import static org.apache.coyote.http11.response.StatusCode.OK;

public class OtherController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        return new Response()
                .addResponseLine(requestReader.getProtocol(), OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByFile(requestReader.getUri());
    }
}
