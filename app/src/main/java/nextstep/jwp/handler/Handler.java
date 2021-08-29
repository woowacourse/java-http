package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Response;

public interface Handler {

    Response message() throws IOException;

    Response getMessage() throws IOException;

    Response postMessage();
}
