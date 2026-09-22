package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

final class HttpHeaders implements Iterable<HttpHeader> {

    private final List<HttpHeader> values;

    private HttpHeaders(final List<HttpHeader> values) {
        this.values = List.copyOf(values);
    }

    static HttpHeaders empty() {
        return new HttpHeaders(List.of());
    }

    HttpHeaders add(final String name, final String value) {
        return add(new HttpHeader(name, value));
    }

    HttpHeaders add(final HttpHeader header) {
        final var added = new ArrayList<>(values);
        added.add(header);
        return new HttpHeaders(added);
    }

    Optional<String> firstValue(final String name) {
        return values.stream()
                .filter(header -> header.name().equalsIgnoreCase(name))
                .map(HttpHeader::value)
                .findFirst();
    }

    @Override
    public Iterator<HttpHeader> iterator() {
        return values.iterator();
    }
}
