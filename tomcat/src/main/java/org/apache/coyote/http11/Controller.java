package org.apache.coyote.http11;

import org.apache.coyote.http11.common.header.ContentTypeValue;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class Controller {

    public ResponseEntity getIndex() {
        return new ResponseEntity(HttpStatus.OK, ContentTypeValue.TEXT_HTML, "Hello world!");
    }
}
