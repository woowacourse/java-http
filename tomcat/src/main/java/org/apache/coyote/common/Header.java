package org.apache.coyote.common;

import java.util.Collection;

public abstract class Header {

    protected final Collection<String> values;

    protected Header(Collection<String> values) {
        this.values = values;
    }

    public void add(String value) {
        values.add(value);
    }

    public void addAll(Collection<String> values) {
        this.values.addAll(values);
    }

    abstract String getValues();
}
