package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

	@AfterEach
	void tearDown() {
		SessionManager.clear();
	}

	@Test
	@DisplayName("sessionManager에 session을 추가한다.")
	void add() {
		final Session session = new Session("unique Id");

		SessionManager.add(session);

		final Session foundSession = SessionManager.findSession(session.getId());
		assertThat(foundSession)
			.usingRecursiveComparison()
			.isEqualTo(session);
	}

	@Test
	@DisplayName("sessionManager에 session을 추가한다.")
	void findSession() {
		final Session session = new Session("unique Id");
		SessionManager.add(session);

		final Session foundSession = SessionManager.findSession(session.getId());

		assertThat(foundSession)
			.usingRecursiveComparison()
			.isEqualTo(session);
	}
}
