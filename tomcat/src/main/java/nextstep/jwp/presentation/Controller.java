package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;

import java.io.IOException;

public interface Controller {

    Response service(RequestReader requestReader) throws IOException;
}
