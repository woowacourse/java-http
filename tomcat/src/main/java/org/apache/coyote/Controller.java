package org.apache.coyote;

import java.io.IOException;

public interface Controller {

    String run(final String queryStrings) throws IOException;
}
