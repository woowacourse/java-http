package study;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexTest {

    @DisplayName("특정 문자열 뒤에 0개 이상의 문자가 나열되는 정규식 - 1개 이상의 문자")
    @Test
    void stringAfterSpecificString_stringExists() {
        final Pattern pattern = Pattern.compile("/login" + ".*");
        final Matcher matcher = pattern.matcher("/login?account=yaho&password=password");

        assertThat(matcher.find()).isTrue();
    }

    @DisplayName("특정 문자열 뒤에 0개 이상의 문자가 나열되는 정규식 - 0개의 문자")
    @Test
    void stringAfterSpecificString_stringNotExists() {
        final Pattern pattern = Pattern.compile("/login" + ".*");
        final Matcher matcher = pattern.matcher("/login");

        assertThat(matcher.find()).isTrue();
    }

    @DisplayName("특정 문자열 뒤에 0개 이상의 문자가 나열되는 정규식 - 특정 문자열이 없는 경우")
    @Test
    void stringAfterSpecificString_noSpecificString() {
        final Pattern pattern = Pattern.compile("/login" + ".*");
        final Matcher matcher = pattern.matcher("/log");

        assertThat(matcher.find()).isFalse();
    }

    @DisplayName("특정 문자열 뒤에 0개 이상의 문자가 나열되는 정규식 - 특정 문자열 앞에 문자열이 있는 경우")
    @Test
    void stringAfterSpecificString_stringBeforeSpecificString() {
        final Pattern pattern = Pattern.compile("/login" + ".*");
        final Matcher matcher = pattern.matcher("api/log");

        assertThat(matcher.find()).isFalse();
    }

    @DisplayName(".을 정규식이 아닌 문자열로 사용")
    @Test
    void useDotToStringNotRegex() {
        final Pattern pattern = Pattern.compile(".*" + "\\." + ".*");
        final Matcher matcher = pattern.matcher("index.html");

        assertThat(matcher.find()).isTrue();
    }


}
