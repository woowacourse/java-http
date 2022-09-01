package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.apache.coyote.http11.request.ResourceLocator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceLocatorTest {

    @Test
    @DisplayName("입력된 파일명과 일치하는 파일을 찾아온다")
    void getResource() {
        ResourceLocator resourceLocator = new ResourceLocator();
        String requestURI = "styles.css";

        File resource = resourceLocator.findResource(requestURI);
        assertThat(resource).isNotNull();
    }

    @Test
    @DisplayName("null을 입력받는 경우 예외를 발생시킨다")
    void throwExceptionWhenInputIsNull() {
        ResourceLocator resourceLocator = new ResourceLocator();
        assertThatThrownBy(() -> resourceLocator.findResource(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("공백을 입력받는 경우 예외를 발생시킨다")
    void throwExceptionWhenInputIsBlank() {
        ResourceLocator resourceLocator = new ResourceLocator();
        assertThatThrownBy(() -> resourceLocator.findResource(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
