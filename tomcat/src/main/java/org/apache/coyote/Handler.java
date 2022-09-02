package org.apache.coyote;

import java.util.Map;

public interface Handler {
    void process(Map<String, String> params);
}
