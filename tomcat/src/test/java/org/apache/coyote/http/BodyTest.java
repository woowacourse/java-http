package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BodyTest {

    @Test
    @DisplayName("Body를 생성한다.(file)")
    void Body_File(){
        assertThat(new Body("/index.html").getValue()).isNotEqualTo("Hello world!");
    }

    @Test
    @DisplayName("Body를 생성한다.(not file)")
    void Body_NotFile(){
        assertThat(new Body("/").getValue()).isEqualTo("Hello world!");
    }
}
