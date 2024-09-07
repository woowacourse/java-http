package org.apache.coyote.http11.path;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static util.StringUtil.findIndexStartIndexOne;

public class Path {
    private static final Map<String, Path> CACHE = new HashMap<>();
    private static final String DELIMITER = "/";

    private final String line;

    public static Path from(final String line) {
        return CACHE.computeIfAbsent(line, Path::new);
    }

    private Path(final String line) {
        this.line = line.startsWith(DELIMITER) ? line : DELIMITER + line;
    }

    public Path next() {
        final int index = findIndexStartIndexOne(line, DELIMITER);
        if (index == -1) {
            throw new PathParseException(String.format("%s is Last Uri", line));
        }
        return new Path(line.substring(findIndexStartIndexOne(line, DELIMITER)));
    }

    public boolean last() {
        return findIndexStartIndexOne(line, DELIMITER) == -1;
    }

    public String value() {
        return line;
    }

    @Override
    public String toString() {
        return "Uri{" +
                "line='" + line + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Path path)) return false;
        return Objects.equals(line, path.line);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(line);
    }
}
