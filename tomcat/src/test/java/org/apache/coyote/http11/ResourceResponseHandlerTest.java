package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceResponseHandlerTest {

    private final ResourceResponseHandler resourceResponseHandler = new ResourceResponseHandler();

    @Test
    void notFoundResource() {
        //given
        final var noResourceUri = "/hi";

        //expect
        assertThatThrownBy(() -> resourceResponseHandler.buildBodyFrom(noResourceUri))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
