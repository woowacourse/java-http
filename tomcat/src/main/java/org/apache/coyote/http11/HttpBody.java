package org.apache.coyote.http11;

import java.util.Optional;

public interface HttpBody {

    Optional<String> get(String key);

    boolean isEmpty();

    int getSize();
}
