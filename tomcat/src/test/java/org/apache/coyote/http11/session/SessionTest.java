package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {
	@DisplayName("쿠키를 사용해 세션을 조회한다.")
	@Test
	void newSession() {
		String sessionId = "0f002ebd-6909-4630-a40c-72f3ce385d03";
		String cookies = "JSESSIONID=" + sessionId;

		Session session = new Session(cookies);

		assertThat(session.getId()).isEqualTo(sessionId);
	}
}