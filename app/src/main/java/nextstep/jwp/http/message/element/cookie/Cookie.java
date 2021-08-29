package nextstep.jwp.http.message.element.cookie;

import java.util.List;
import java.util.Optional;

public interface Cookie {
    Optional<String> get(String key);
    List<String> getKeys();
    void put(String key, String value);
    int size();
    String asString();
}
