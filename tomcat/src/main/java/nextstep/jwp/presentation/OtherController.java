package nextstep.jwp.presentation;

import org.apache.coyote.http11.RequestReader;
import org.apache.coyote.http11.Response;
import org.apache.coyote.http11.StatusCode;

import java.io.IOException;

public class OtherController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        return new Response(requestReader, StatusCode.OK)
                .addBaseHeader()
                .createBodyByFile(requestReader.getRequestUrl());
    }
}
