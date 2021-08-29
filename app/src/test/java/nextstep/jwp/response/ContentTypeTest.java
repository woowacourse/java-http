package nextstep.jwp.response;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.response.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ContentTypeTest {

    @DisplayName("uri에 따른 content-type 분류")
    @Test
    void contentTypeByUri() {
        String uri = "index.html";
        String favicon = "favicon.ico";

        ContentType contentType = ContentType.findByUri(uri);
        ContentType faviconIco = ContentType.findByUri(favicon);

        assertThat(contentType).isEqualTo(ContentType.HTML);
        assertThat(faviconIco).isEqualTo(ContentType.ICO);
    }


}
