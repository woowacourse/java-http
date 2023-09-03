package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

	private final SessionManager SESSION_MANAGER = SessionManager.getInstance();

	@AfterEach
	void tearDown() {
		SESSION_MANAGER.clear();
	}

	@Test
	@DisplayName("sessionManager에 session을 추가한다.")
	void add() {
		final Session session = new Session("unique Id");

		SESSION_MANAGER.add(session);

		final Session foundSession = SESSION_MANAGER.findSession(session.getId());
		assertThat(foundSession)
			.usingRecursiveComparison()
			.isEqualTo(session);
	}

	@Test
	@DisplayName("sessionManager에 session을 추가한다.")
	void findSession() {
		final Session session = new Session("unique Id");
		SESSION_MANAGER.add(session);

		final Session foundSession = SESSION_MANAGER.findSession(session.getId());

		assertThat(foundSession)
			.usingRecursiveComparison()
			.isEqualTo(session);
	}
}
