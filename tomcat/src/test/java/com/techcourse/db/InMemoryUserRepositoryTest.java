package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.techcourse.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("기본 사용자 gugu를 조회할 수 있다")
    void findByAccount_existing() {
        final Optional<User> found = InMemoryUserRepository.findByAccount("gugu");
        assertSoftly(softly -> {
            softly.assertThat(found).isPresent();
            found.ifPresent(u -> softly.assertThat(u.getAccount()).isEqualTo("gugu"));
        });
    }

    @Test
    @DisplayName("존재 여부 확인 - 존재하는 계정은 true")
    void existsByAccount_true() {
        assertThat(InMemoryUserRepository.existsByAccount("gugu")).isTrue();
    }

    @Test
    @DisplayName("ID가 없는 사용자 저장 시 ID가 부여되고 조회 가능")
    void save_withoutId_persists() {
        final String account = "user_without_id_1";
        final User toSave = User.withoutId(account, "pw", "a@b.c");

        final User saved = InMemoryUserRepository.save(toSave);

        assertSoftly(softly -> {
            softly.assertThat(saved.getId()).isNotNull();
            softly.assertThat(saved.getAccount()).isEqualTo(account);
            softly.assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
        });
    }

    @Test
    @DisplayName("중복 계정 저장 시 예외 발생")
    void save_duplicateAccount_throws() {
        final User duplicate = User.withoutId("gugu", "pw", "x@y.z");

        assertThatThrownBy(() -> InMemoryUserRepository.save(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 아이디");
    }

    @Test
    @DisplayName("이미 ID가 있는 새 계정은 그대로 저장(업데이트)된다")
    void save_withId_newAccount_putsAsIs() {
        final String account = "persisted_new_account_1";
        final User persisted = User.withId(9999L, account, "pw", "p@p.p");

        final User saved = InMemoryUserRepository.save(persisted);

        assertSoftly(softly -> {
            softly.assertThat(saved.getId()).isEqualTo(9999L);
            softly.assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
        });
    }
}
