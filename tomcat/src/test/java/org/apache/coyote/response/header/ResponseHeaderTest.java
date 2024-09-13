package org.apache.coyote.response.header;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.util.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    private static final String CRLF = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: 80 ";
    private static final String LOCATION = "Location: /index.html ";
    private static final String CONTENT_TYPE_TEXT_CHARSET_UTF_8 = "Content-Type: text/html;charset=utf-8 ";
    private static final String INDEX_HTML = "/index.html";

    @Test
    @DisplayName("Header를 형식에 맞게 조합하여 가져올 수 있다.")
    void toCombinedHeader() {
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.addLocation(INDEX_HTML);
        responseHeader.addContentLengthAndType(ContentType.HTML, 80);

        assertEquals(responseHeader.toCombinedHeader(),
                String.join(CRLF,
                        CONTENT_LENGTH,
                        LOCATION,
                        CONTENT_TYPE_TEXT_CHARSET_UTF_8));
    }
}
