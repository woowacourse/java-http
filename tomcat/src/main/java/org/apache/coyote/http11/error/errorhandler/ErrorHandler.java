package org.apache.coyote.http11.error.errorhandler;

import java.util.Map;

public interface ErrorHandler {
    Map<String, String> handleError();
}
