package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public interface Handler {

    Response message(Request request) throws IOException;
}
