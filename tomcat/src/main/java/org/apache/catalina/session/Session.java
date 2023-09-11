package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

  private final String id;
  private final Map<String, Object> values = new ConcurrentHashMap<>();

  public Session(final String id) {
    this.id = id;
  }

  public static Session generateSession() {
    return new Session(UUID.randomUUID().toString());
  }

  public String getId() {
    return this.id;
  }

  public Object getAttribute(final String name) {
    return this.values.get(name);
  }

  public void setAttribute(final String name, final Object value) {
    this.values.put(name, value);
  }

  public void removeAttribute(final String name) {
    this.values.remove(name);
  }

  public void invalidate() {
    this.values.clear();
  }
}
