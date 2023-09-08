package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;

import java.io.IOException;

public class OtherController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        return new Response(StatusCode.OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByFile(requestReader.getUri());
    }
}
