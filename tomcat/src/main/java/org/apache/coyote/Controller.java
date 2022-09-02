package org.apache.coyote;

import java.util.Map;

public interface Controller {
    String process(Map<String, String> params);
}
