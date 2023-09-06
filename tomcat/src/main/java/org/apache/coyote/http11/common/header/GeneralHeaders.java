package org.apache.coyote.http11.common.header;

import java.util.Map;

public class GeneralHeaders extends Headers {

    public GeneralHeaders() {
    }

    public GeneralHeaders(final Map<HeaderName, String> headers) {
        super(headers);
    }

    @Override
    public boolean isType(final HeaderName headerName) {
        return HeaderName.isGeneralHeaders(headerName);
    }
}
