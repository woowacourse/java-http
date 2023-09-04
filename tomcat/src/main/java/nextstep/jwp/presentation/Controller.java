package nextstep.jwp.presentation;

import org.apache.coyote.http11.RequestReader;
import org.apache.coyote.http11.Response;

import java.io.IOException;

public interface Controller {

    Response service(RequestReader requestReader) throws IOException;
}
