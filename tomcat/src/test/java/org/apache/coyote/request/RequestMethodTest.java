package org.apache.coyote.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class RequestMethodTest {

    @Test
    void 지정한_메서드가_아니라면_GET_메서드로_가정한다() {
        final RequestMethod requestMethod = RequestMethod.get("KERO");
        Assertions.assertThat(requestMethod).isEqualTo(RequestMethod.GET);
    }
}
