package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Header {

    private final String name;
    private final List<String> value;

    public Header(final String name, final List<String> value) {
        this.name = name;
        this.value = new ArrayList<>(value);
    }

    public Header(final String name, final String valueString) {
        this(name, parseValues(valueString));
    }

    private static List<String> parseValues(final String valueString) {
        if (valueString.contains(";")) {
            final String[] parts = valueString.split(";");
            return Arrays.stream(parts).map(String::trim).toList();
        }
        return List.of(valueString);
    }

    public void addValue(final String newValue) {
        value.addAll(parseValues(newValue));
    }

    public void addValue(final List<String> values) {
        values.forEach(this::addValue);
    }

    public void addValue(final Header other) {
        if (this.name.equalsIgnoreCase(other.name)) {
            addValue(other.value);
            return;
        }
        throw new IllegalArgumentException("헤더 이름이 일치하지 않아 값을 추가할 수 없습니다.");
    }

    public String getValueString() {
        return String.join("; ", value);
    }

    public String toHeaderString() {
        return name + ": " + getValueString() + " ";
    }

    public String getName() {
        return name;
    }
}
