package com.techcourse.web.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    private Session session;

    @BeforeEach
    void setUp() {
        session = new Session();
    }

    @Test
    @DisplayName("세션 생성 시 고유한 ID 할당")
    void sessionCreationAssignsUniqueId() {
        // given
        final Session session1 = new Session();
        final Session session2 = new Session();

        // when & then
        assertSoftly(softly -> {
            softly.assertThat(session1.getId()).isNotNull();
            softly.assertThat(session2.getId()).isNotNull();
            softly.assertThat(session1.getId()).isNotEqualTo(session2.getId());

            // UUID 형식인지 확인 (36자리, 하이픈 포함)
            softly.assertThat(session1.getId()).hasSize(36);
            softly.assertThat(session2.getId()).hasSize(36);
            softly.assertThat(session1.getId())
                    .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        });
    }

    @Test
    @DisplayName("속성 설정 및 조회")
    void setAndGetAttribute() {
        // given
        final String attributeName = "username";
        final String attributeValue = "gugu";

        // when
        session.setAttribute(attributeName, attributeValue);

        // then
        final Object retrievedValue = session.getAttribute(attributeName);
        assertThat(retrievedValue).isEqualTo(attributeValue);
    }

    @Test
    @DisplayName("여러 속성 설정 및 조회")
    void setAndGetMultipleAttributes() {
        // given
        final String username = "gugu";
        final Integer age = 25;
        final Boolean isAdmin = true;

        // when
        assertSoftly(softly -> {
            session.setAttribute("username", username);
            session.setAttribute("age", age);
            session.setAttribute("isAdmin", isAdmin);

            // then
            assertThat(session.getAttribute("username")).isEqualTo(username);
            assertThat(session.getAttribute("age")).isEqualTo(age);
            assertThat(session.getAttribute("isAdmin")).isEqualTo(isAdmin);
        });
    }

    @Test
    @DisplayName("속성 덮어쓰기")
    void overwriteAttribute() {
        // given
        final String attributeName = "username";
        final String initialValue = "gugu";
        final String newValue = "brown";

        // when
        session.setAttribute(attributeName, initialValue);
        session.setAttribute(attributeName, newValue);

        // then
        final Object retrievedValue = session.getAttribute(attributeName);
        assertSoftly(softly -> {
            softly.assertThat(retrievedValue).isEqualTo(newValue);
            softly.assertThat(retrievedValue).isNotEqualTo(initialValue);
        });
    }

    @Test
    @DisplayName("존재하지 않는 속성 조회 시 null 반환")
    void getNonExistentAttribute() {
        // when
        final Object retrievedValue = session.getAttribute("nonExistentAttribute");

        // then
        assertThat(retrievedValue).isNull();
    }

    @Test
    @DisplayName("속성 제거")
    void removeAttribute() {
        // given
        final String attributeName = "username";
        final String attributeValue = "gugu";
        session.setAttribute(attributeName, attributeValue);

        // 속성이 설정되었는지 확인
        assertThat(session.getAttribute(attributeName)).isEqualTo(attributeValue);

        // when
        session.removeAttribute(attributeName);

        // then
        final Object retrievedValue = session.getAttribute(attributeName);
        assertThat(retrievedValue).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 속성 제거 시 예외 없이 처리")
    void removeNonExistentAttribute() {
        // when & then
        // 예외가 발생하지 않아야 함
        session.removeAttribute("nonExistentAttribute");
    }

    @Test
    @DisplayName("null 값으로 속성 설정 시 속성 제거")
    void setAttributeWithNullValueRemovesAttribute() {
        // given
        final String attributeName = "username";
        session.setAttribute(attributeName, "gugu");

        // 속성이 설정되었는지 확인
        assertThat(session.getAttribute(attributeName)).isEqualTo("gugu");

        // when
        session.setAttribute(attributeName, null);

        // then
        final Object retrievedValue = session.getAttribute(attributeName);
        assertThat(retrievedValue).isNull();
    }

    @Test
    @DisplayName("null 속성 이름으로 설정 시 예외 발생")
    void setAttributeWithNullName() {
        // when & then
        assertThatThrownBy(() -> session.setAttribute(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("빈 속성 이름으로 설정 시 예외 발생")
    void setAttributeWithEmptyName() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> session.setAttribute("", "value"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");

            softly.assertThatThrownBy(() -> session.setAttribute("   ", "value"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");
        });
    }

    @Test
    @DisplayName("null 속성 이름으로 조회 시 예외 발생")
    void getAttributeWithNullName() {
        // when & then
        assertThatThrownBy(() -> session.getAttribute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("빈 속성 이름으로 조회 시 예외 발생")
    void getAttributeWithEmptyName() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> session.getAttribute(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");

            softly.assertThatThrownBy(() -> session.getAttribute("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");
        });
    }

    @Test
    @DisplayName("null 속성 이름으로 제거 시 예외 발생")
    void removeAttributeWithNullName() {
        // when & then
        assertThatThrownBy(() -> session.removeAttribute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("빈 속성 이름으로 제거 시 예외 발생")
    void removeAttributeWithEmptyName() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> session.removeAttribute(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");

            softly.assertThatThrownBy(() -> session.removeAttribute("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("속성 이름은 null이거나 비어있을 수 없습니다");
        });
    }

    @Test
    @DisplayName("다양한 타입의 속성값 저장")
    void storeVariousTypeAttributes() {
        // given
        final String stringValue = "test";
        final Integer intValue = 42;
        final Boolean boolValue = true;
        final Double doubleValue = 3.14;

        // when
        session.setAttribute("string", stringValue);
        session.setAttribute("integer", intValue);
        session.setAttribute("boolean", boolValue);
        session.setAttribute("double", doubleValue);

        // then
        assertSoftly(softly -> {
            softly.assertThat(session.getAttribute("string")).isEqualTo(stringValue);
            softly.assertThat(session.getAttribute("integer")).isEqualTo(intValue);
            softly.assertThat(session.getAttribute("boolean")).isEqualTo(boolValue);
            softly.assertThat(session.getAttribute("double")).isEqualTo(doubleValue);
        });
    }

    @Test
    @DisplayName("복잡한 객체 속성값 저장")
    void storeComplexObjectAttributes() {
        // given
        final java.util.List<String> listValue = java.util.Arrays.asList("a", "b", "c");
        final java.util.Map<String, String> mapValue = java.util.Map.of("key", "value");

        // when
        session.setAttribute("list", listValue);
        session.setAttribute("map", mapValue);

        // then
        assertSoftly(softly -> {
            softly.assertThat(session.getAttribute("list")).isEqualTo(listValue);
            softly.assertThat(session.getAttribute("map")).isEqualTo(mapValue);
        });
    }

    @Test
    @DisplayName("세션 ID는 변경되지 않음")
    void sessionIdIsImmutable() {
        // given
        final String initialId = session.getId();

        // when
        session.setAttribute("someAttribute", "someValue");
        session.removeAttribute("someAttribute");

        // then
        assertThat(session.getId()).isEqualTo(initialId);
    }
}
