package org.apache.coyote.http11.util;

import org.apache.coyote.http11.constants.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ContentTypeResolverTest {

    @Test
    void resolve() {
        final ContentType contentType = ContentTypeResolver.resolve("/test.css", "text/css");
        Assertions.assertThat(contentType).isEqualTo(ContentType.CSS);
    }
}
