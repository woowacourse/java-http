package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;

public interface Controller {

    Response service(RequestReader requestReader) throws IOException;
}
