package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;

public class IndexController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        return new Response(requestReader, StatusCode.OK).
                createResponseBodyByFile(requestReader.getRequestUrl()).
                addBaseHeaders();
    }
}
