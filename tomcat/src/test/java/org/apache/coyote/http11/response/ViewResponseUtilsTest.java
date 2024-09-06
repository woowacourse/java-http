package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.view.View;
import org.apache.coyote.http11.response.view.ViewType;
import org.junit.jupiter.api.Test;

class ViewResponseUtilsTest {

    @Test
    void createResponseTest() {
        View view = new View(ViewType.HTML, "Hello world!");
        String expected  = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        String actual = ViewResponseUtils.createResponse(view);

        assertThat(actual).isEqualTo(expected);
    }
}
