package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AcceptTest {

    @Test
    void ACCEPT_추출한다() {
        final String[] accepts = new String[]{"text/html"};

        final Accept accept = Accept.from(accepts);

        assertThat(accept.getAcceptType()).isEqualTo("text/html");
    }
}
