package nextstep.jwp.framework.domain;

import java.io.InputStream;

public interface NetworkHandler {

    ParseResult parseRequest(InputStream inputStream);
}
