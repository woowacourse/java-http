package org.apache.coyote.http11.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeparatorTest {

    @DisplayName("리스트로 전달한 값들을 지정한 구분자로 구분해 반환한다.")
    @Test
    void separateKeyValue() {
        List<String> targets = List.of("key1=value1", "key2=value2");
        String delimiter = "=";

        Map<String, String> result = Separator.separateKeyValueBy(targets, delimiter);

        assertThat(result).containsEntry("key1", "value1")
                .containsEntry("key2", "value2");
    }

    @DisplayName("주어진 텍스트에 구분자가 여러개인 경우, 가장 첫번째 구분자를 기준으로 나눈다.")
    @Test
    void separateKeyValueFirstDelimiter() {
        List<String> targets = List.of("key1=value1=hello");
        String delimiter = "=";

        Map<String, String> result = Separator.separateKeyValueBy(targets, delimiter);

        assertThat(result).containsEntry("key1", "value1=hello");
    }

    @DisplayName("입력한 텍스트에 존재하지 않는 구분자를 입력하는 경우, 예외가 발생한다.")
    @Test
    void separateKeyValueInvalidDelimiter() {
        List<String> targets = List.of("key1=value1");
        String invalidDelimiter = ":";

        assertThatThrownBy(() -> Separator.separateKeyValueBy(targets, invalidDelimiter))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only Key-Value pair can be separated.");
    }

    @DisplayName("입력한 텍스트에 존재하지 않는 구분자를 입력하는 경우, 예외가 발생한다.")
    @Test
    void separateKeyValueEmptyKey() {
        List<String> targets = List.of("=value1");
        String invalidDelimiter = "=";

        assertThatThrownBy(() -> Separator.separateKeyValueBy(targets, invalidDelimiter))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only Key-Value pair can be separated.");
    }
}
