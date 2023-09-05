package org.apache.coyote.http11.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Cookies implements Iterable<Cookie> {
    private List<Cookie> cookies = new ArrayList<>();

    @Override
    public Iterator<Cookie> iterator() {
        return cookies.iterator();
    }

    public void add(String key, String value) {
        cookies.add(new Cookie(key, value));
    }

    public Optional<String> get(String key) {
        final Optional<Cookie> cookie = cookies.stream()
                .filter(coo -> coo.isKeyName(key))
                .findAny();
        return cookie.map(Cookie::getValue);
    }
}
