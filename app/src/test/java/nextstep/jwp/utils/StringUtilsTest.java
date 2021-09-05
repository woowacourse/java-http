package nextstep.jwp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilsTest {

    @DisplayName("문자열을 구분자를 기준으로 리스트로 분리한다.")
    @Test
    void splitWithSeparator() {
        String s = "aaaa\r\nb\r\ncc\r\n";
        List<String> pieces = StringUtils.splitWithSeparator(s, "\r\n");
        assertThat(pieces).containsExactly("aaaa", "b", "cc");
    }

    @DisplayName("문자열을 첫 구분자를 기준으로 두 조각으로 분리한다.")
    @Test
    void splitTwoPiecesWithSeparator() {
        String s = "aaaa\r\nb\r\ncc\r\n";
        List<String> pieces = StringUtils.splitTwoPiecesWithSeparator(s, "\r\n");
        assertThat(pieces).containsExactly("aaaa", "b\r\ncc\r\n");
    }

    @DisplayName("key + value 형태가 여러번 반복되는 문자열의 경우 맵으로 추출한다.")
    @Test
    void extractMap() {
        // given
        LinkedHashMap<String, String> expected = new LinkedHashMap<>();
        expected.put("a", "1");
        expected.put("b", "2");
        expected.put("c", "3=3");

        String s = " a = 1; b= 2 ; c  = 3=3   ;  ";
        String pieceSeparator = ";";
        String paramSeparator = "=";

        // when
        Map<String, String> map = StringUtils.extractMap(s, pieceSeparator, paramSeparator);

        // then
        assertThat(map).isEqualTo(expected);
    }

    @DisplayName("공백 문자열을 맵으로 추출 시 비어 있는 맵이 생성된다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void extractMapWithBlank(String s) {
        // given
        String pieceSeparator = ";";
        String paramSeparator = "=";

        // when
        Map<String, String> map = StringUtils.extractMap(s, pieceSeparator, paramSeparator);

        // then
        assertThat(map).isEmpty();
    }

    @DisplayName("널이나 비어있는 문자열의 Piece Separator 로 맵 추출 시 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void extractMapWithInvalidPieceSeparator(String pieceSeparator) {
        assertThatThrownBy(() -> StringUtils.extractMap("a:1;b=c", pieceSeparator, ":"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("separator 가 없거나 빈 문자열이면 안 됩니다");
    }

    @DisplayName("널이나 비어있는 문자열의 Param Separator 로 맵 추출 시 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void extractMapWithInvalidParamSeparator(String paramSeparator) {
        assertThatThrownBy(() -> StringUtils.extractMap("a:1;b=c", ";", paramSeparator))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("separator 가 없거나 빈 문자열이면 안 됩니다");
    }

    @DisplayName("여러 단어를 공백으로 합쳐준다.")
    @Test
    void joinWithBlank() {
        String s = StringUtils.joinWithBlank("hello", "world", "goodbye", "world");
        assertThat(s).isEqualTo("hello world goodbye world");
    }

    @DisplayName("문자열에 뉴라인을 붙인다.")
    @Test
    void concatNewLine() {
        String expect = "hello world\r\n";
        String s = "hello world";
        assertThat(StringUtils.concatNewLine(s)).isEqualTo(expect);
    }
}
