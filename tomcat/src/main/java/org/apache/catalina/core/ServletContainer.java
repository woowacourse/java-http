package org.apache.catalina.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ServletContainer {

    private final Map<String, Servlet> values = new ConcurrentHashMap<>();

    public void registerServlet(final String uri, final Servlet servlet) {
        values.put(uri, servlet);
    }

    public Optional<Servlet> findServlet(final String uri) {
        return getSortedEntries()
                .filter(entry -> uri.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private Stream<Map.Entry<String, Servlet>> getSortedEntries() {
        return values.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getKey().compareTo(o1.getKey()));
    }
}
