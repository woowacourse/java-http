package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotFoundException;
import org.junit.jupiter.api.Test;

class FileReaderTest {

    @Test
    void read() {
        String actual = FileReader.read("/index.html");

        assertThat(actual).startsWith("<!DOCTYPE html>\n");
    }

    @Test
    void fail() {
        assertThatThrownBy(() -> FileReader.read("index.html"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("File not found.");
    }
}
