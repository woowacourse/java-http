package org.apache.coyote.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommaSeparateHeader extends Header {

    public static final Header EMPTY = new CommaSeparateHeader(Collections.emptyList());

    public CommaSeparateHeader() {
        super(new ArrayList<>());
    }

    private CommaSeparateHeader(List<String> values) {
        super(values);
    }

    @Override
    public String getValues() {
        return String.join(", ", values);
    }
}
