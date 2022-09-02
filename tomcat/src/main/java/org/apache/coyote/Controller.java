package org.apache.coyote;

import java.util.Map;

public interface Controller {
    void process(Map<String, String> params);
}
