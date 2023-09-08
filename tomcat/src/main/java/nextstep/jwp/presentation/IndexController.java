package nextstep.jwp.presentation;

import org.apache.coyote.http11.RequestReader;
import org.apache.coyote.http11.Response;

import java.io.IOException;

import static org.apache.coyote.http11.StatusCode.OK;

public class IndexController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        return new Response(requestReader, OK)
                .addBaseHeader()
                .createBodyByFile(requestReader.getUri());
    }
}
