package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cookies implements Iterable<Cookie> {
    private List<Cookie> cookies = new ArrayList<>();

    @Override
    public Iterator<Cookie> iterator() {
        return cookies.iterator();
    }

    public void add(String key, String value) {
        cookies.add(new Cookie(key, value));
    }
}
