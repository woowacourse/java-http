package jakarta.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceFinderTest {

    @ParameterizedTest
    @CsvSource(value = {"sample.txt:true", "unknown.txt:false"}, delimiter = ':')
    @DisplayName("리소스가 존재하는지 확인할 수 있다.")
    void hasResource(String source, boolean expected) {
        ResourceFinder resourceFinder = new ResourceFinder();
        boolean result = resourceFinder.hasResource(source);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("리소스를 불러올 수 있다.")
    void read() {
        ResourceFinder resourceFinder = new ResourceFinder();
        byte[] result = resourceFinder.readResource("sample.txt");
        String resultString = new String(result);

        assertThat(resultString).isEqualTo("sample");
    }

    @Test
    @DisplayName("리소스가 존재하지 않으면 불러올 수 없다.")
    void readNonExistsResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        assertThatThrownBy(() -> resourceFinder.readResource("unknown.txt"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 리소스입니다.");
    }
}
