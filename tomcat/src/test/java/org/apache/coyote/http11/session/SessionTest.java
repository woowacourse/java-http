package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

	@Test
	@DisplayName("id를 key로 하여 object를 저장할 수 있다.")
	void setAttributes() {
		final String key = "key";
		final String object = "object";
		final Session session = new Session("id");

		session.setAttributes(key, object);

		assertThat(session.getAttributes(key))
			.isEqualTo(object);
	}

	@Test
	@DisplayName("id를 key로 하여 object를 조회할 수 있다.")
	void getAttributes() {
		final String key = "key";
		final String object = "object";
		final Session session = new Session("id");
		session.setAttributes(key, object);

		final Object actual = session.getAttributes(key);

		assertThat(actual)
			.isEqualTo(object);
	}
}
