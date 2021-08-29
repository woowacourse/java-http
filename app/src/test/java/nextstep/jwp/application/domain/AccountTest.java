package nextstep.jwp.application.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTest {

    @DisplayName("빈 계정명은 예외")
    @Test
    void emptyAccountTest() {
        //given
        String empty ="";
        String onlyBlank = "    ";
        //when
        //then
        assertThatThrownBy(() -> new Account(empty))
            .hasMessageContaining("계정명은 비어있을 수 없습니다.");
        assertThatThrownBy(() -> new Account(onlyBlank))
            .hasMessageContaining("계정명은 비어있을 수 없습니다.");
    }

    @DisplayName("유저명 VO 생성")
    @Test
    void createTest() {
        //given
        String wedge = "wedge";
        //when
        Account account = new Account(wedge);
        //then
        assertThat(account.value()).isEqualTo(wedge);
    }
}