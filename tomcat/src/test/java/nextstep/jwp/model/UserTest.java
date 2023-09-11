package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTest {

    @Test
    void 사용자를_생성할_때_빈_정보를_입력하는_경우_예외를_던진다() {
        // expect
        assertThatThrownBy(() -> new User("", "password", "email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 사용자 정보를 입력해주세요.");
    }
}
