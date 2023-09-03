package org.apache.coyote;

import org.apache.coyote.domain.HttpRequestHeader;

import java.io.IOException;

public abstract class Controller {

    public abstract String run(final HttpRequestHeader httpRequestHeader) throws IOException;

}
