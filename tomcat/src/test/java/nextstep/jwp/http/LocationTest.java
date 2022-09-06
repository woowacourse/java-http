package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LocationTest {

    @Test
    void 빈_Location을_생성한다() {
        Location expected = new Location("");
        Location actual = Location.empty();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 빈_Location이면_빈_헤더포맷을_반환한다() {
        String expected = "";
        Location location = Location.empty();
        String actual = location.toHeaderFormat();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void Location의_헤더포맷을_반환한다() {
        String expected = "Location: /index.html ";
        Location location = new Location("/index.html");
        String actual = location.toHeaderFormat();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void Location이_비었는지_확인한다() {
        Location location = new Location("");
        boolean actual = location.isEmpty();

        assertThat(actual).isTrue();
    }
}
