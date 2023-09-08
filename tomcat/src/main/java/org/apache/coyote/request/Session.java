package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

  private final String id;
  private final Map<String, Object> values = new HashMap<>();

  public Session(final String id) {
    this.id = id;
  }

  public Optional<Object> getAttribute(final String name) {
    return Optional.ofNullable(values.get(name));
  }

  public void setAttribute(final String name, final Object value) {
    values.put(name, value);
  }

  public String getId() {
    return id;
  }
}
