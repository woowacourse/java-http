package nextstep.jwp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private Map<String, String> map;

    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put("account", "ggyool");
        map.put("password", "password");
        map.put("email", "ggyool@never.com");
    }

    @DisplayName("map으로 User를 생성한다.")
    @Test
    void createWithMap() {
        User user = User.createWithMap(map);

        assertThat(user.getId()).isEqualTo(0);
        assertThat(user.getAccount()).isEqualTo("ggyool");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getEmail()).isEqualTo("ggyool@never.com");
    }

    @DisplayName("map으로 User를 생성에 실패한다. (account 없음)")
    @Test
    void createWithNoAccountMap() {
        map.remove("account");
        assertThatThrownBy(() -> User.createWithMap(map))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("필수값이 없어서 User 생성에 실패했습니다")
                .hasMessageContaining("account");
    }

    @DisplayName("map으로 User를 생성에 실패한다. (password 없음")
    @Test
    void createWithNoPasswordMap() {
        map.remove("password");
        assertThatThrownBy(() -> User.createWithMap(map))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("필수값이 없어서 User 생성에 실패했습니다")
                .hasMessageContaining("password");
    }

    @DisplayName("map으로 User를 생성에 실패한다. (email 없음")
    @Test
    void createWithNoEmailMap() {
        map.remove("email");
        assertThatThrownBy(() -> User.createWithMap(map))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("필수값이 없어서 User 생성에 실패했습니다")
                .hasMessageContaining("email");
    }
}
