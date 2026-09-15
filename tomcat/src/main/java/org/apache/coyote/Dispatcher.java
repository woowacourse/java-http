package org.apache.coyote;

import java.util.Map;
import java.util.Optional;

public interface Dispatcher {

    Optional<String> dispatch(String path, Map<String, String> params);
}
