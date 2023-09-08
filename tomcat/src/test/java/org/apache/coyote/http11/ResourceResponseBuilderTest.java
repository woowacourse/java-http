package org.apache.coyote.http11;

import org.apache.coyote.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceResponseBuilderTest {

    @Test
    void notFoundResource() {
        //given
        final var noResourceUri = "/hi";
        final var url = getClass().getResource("static" + noResourceUri);

        //expect
        assertThatThrownBy(() -> ResourceResponseBuilder.build(url))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
