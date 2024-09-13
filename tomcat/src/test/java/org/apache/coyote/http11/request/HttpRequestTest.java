package org.apache.coyote.http11.request;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.common.VersionOfProtocol;
import org.apache.coyote.http11.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {
	private BufferedReader reader;

	@BeforeEach
	public void setUp() {
		String httpRequest =
			"GET /test/path HTTP/1.1\r\n" +
				"Host: localhost\r\n" +
				"Content-Length: 13\r\n" +
				"Cookie: SESSION_ID=abc123\r\n" +
				"\r\n" +
				"property=value";
		reader = new BufferedReader(new StringReader(httpRequest));
	}

	@Test
	public void testHttpRequest() throws IOException {
		HttpRequest httpRequest = new HttpRequest(reader);

		assertEquals("GET", httpRequest.getMethod().value());
		assertTrue(httpRequest.getMethod().isGet());
		assertFalse(httpRequest.getMethod().isPost());

		assertEquals("/test/path", httpRequest.getPath().value());

		VersionOfProtocol version = new VersionOfProtocol("HTTP/1.1");
		assertEquals(version, new VersionOfProtocol("HTTP/1.1"));

		assertNotNull(httpRequest.getBody());

		Session session = httpRequest.getSession();
		assertNotNull(session);
	}
}