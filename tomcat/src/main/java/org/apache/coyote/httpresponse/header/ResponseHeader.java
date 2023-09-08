package org.apache.coyote.httpresponse.header;

public interface ResponseHeader {

    String getKeyAndValue(final ResponseHeaderType headerType);
}
