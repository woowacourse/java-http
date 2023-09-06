package org.apache.coyote.common;

import java.util.ArrayList;

public class SemicolonSeparateHeader extends Header {

    public SemicolonSeparateHeader() {
        super(new ArrayList<>());
    }

    @Override
    public String getValues() {
        return String.join("; ", values);
    }
}
