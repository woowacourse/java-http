package org.apache.coyote.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

  private final Map<String, String> value;

  private RequestBody(final Map<String, String> value) {
    this.value = value;
  }

  public static RequestBody from(final String bodyValue) {
    if (bodyValue.isEmpty()) {
      return new RequestBody(Collections.emptyMap());
    }

    final String[] split = bodyValue.split("&");

    Map<String, String> body = new HashMap<>();

    for (final String s : split) {
      final String[] split1 = s.split("=");

      body.put(split1[0], split1[1]);
    }

    return new RequestBody(body);
  }

  public boolean isMatching(final String key, final String value) {
    return this.value.get(key).equals(value);
  }

  public String getValue(final String key) {
    return value.get(key);
  }
}
