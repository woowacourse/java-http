package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource({
            "application/x-www-form-urlencoded, FORM_URLENCODED",
            "'application/json; charset=UTF-8', JSON",
            "image/png, PLAIN"
    })
    void from은_헤더에_해당하는_상수를_반환한다(String header, ContentType expected) {
        assertThat(ContentType.from(header)).isEqualTo(expected);
    }
}
