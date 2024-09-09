package org.apache.coyote.http11;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void url을_통해_CSS_ContentType을_가져온다() {
        String url = "/css/styles.css";

        assertThat(ContentType.findByPath(url)).isEqualTo(ContentType.CSS);
    }

    @Test
    void url을_통해_JS_ContentType을_가져온다() {
        String url = "/js/script.js";

        assertThat(ContentType.findByPath(url)).isEqualTo(ContentType.JS);
    }

    @Test
    void url을_통해_HTML_ContentType을_가져온다() {
        String url = "/index.html";

        assertThat(ContentType.findByPath(url)).isEqualTo(ContentType.HTML);
    }
}
