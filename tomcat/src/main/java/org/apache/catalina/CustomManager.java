package org.apache.catalina;

import java.util.Optional;

public interface CustomManager {

    void add(final String jsessionid, final String key, final Object value);

    Optional<Object> find(final String jsessionid, final String key);
}
