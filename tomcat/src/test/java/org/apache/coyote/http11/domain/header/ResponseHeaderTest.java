package org.apache.coyote.http11.domain.header;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http11.domain.body.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    @Test
    @DisplayName("저장된 Header를 조합하여 가져온다.")
    void toCombinedHeader() {
        ResponseHeader responseHeader = new ResponseHeader(80, ContentType.HTML);

        assertEquals(responseHeader.toCombinedHeader(),
                String.join("\r\n",
                        "Content-Length: 80 ",
                        "Content-Type: text/html;charset=utf-8 "
                ));
    }
}
