package org.apache.coyote.http;

import java.util.ArrayList;

public class CommaSeperatedHeader extends Header {

    protected CommaSeperatedHeader() {
        super(new ArrayList<>());
    }

    @Override
    String getValues() {
        return String.join(", ", values);
    }
}
