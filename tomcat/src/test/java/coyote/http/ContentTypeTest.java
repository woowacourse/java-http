package coyote.http;

import org.apache.coyote.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContentTypeTest {

    @Test
    void findType() {
        //given
        List<String> path = List.of("/css/style.css", "/favicon.ico", "/js/index.js", "/index.html");
        List<String> contentType = List.of("text/css", "text/css", "text/javascript", "text/html; charset=utf-8");

        //when
        String type = ContentType.findType(path.get(0));

        //then
        for (int index = 0; index < path.size(); index++) {
            assertEquals(contentType.get(index), ContentType.findType(path.get(index)));
        }
    }

}
