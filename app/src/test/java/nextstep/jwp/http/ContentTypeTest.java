package nextstep.jwp.http;

import nextstep.jwp.http.exception.UnsupportedExtensionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentTypeTest {
    @DisplayName("UnsupportedExtensionException 테스트")
    @Test
    void unsupportedExtensionException() {

        assertThatThrownBy(() -> {
           ContentType.findContentType("test");
        }).isInstanceOf(UnsupportedExtensionException.class);
    }

}