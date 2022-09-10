package org.apache.coyote;

import org.apache.coyote.http11.common.HttpRequest;

public interface RequestMapping {

    Controller findController(final HttpRequest request);
}
