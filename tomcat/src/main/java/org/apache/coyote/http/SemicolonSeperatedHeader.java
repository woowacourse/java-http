package org.apache.coyote.http;

import java.util.ArrayList;

public class SemicolonSeperatedHeader extends Header {

    protected SemicolonSeperatedHeader() {
        super(new ArrayList<>());
    }

    @Override
    String getValues() {
        return String.join("; ", values);
    }
}
