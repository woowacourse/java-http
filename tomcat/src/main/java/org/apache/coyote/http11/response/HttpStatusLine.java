package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.StatusCode;
import org.apache.coyote.http11.common.Version;

public class HttpStatusLine {

	private static final String GAP = " ";

	private final Version version;
	private final StatusCode statusCode;

	private HttpStatusLine(Version version, StatusCode statusCode) {
		this.version = version;
		this.statusCode = statusCode;
	}

	public static HttpStatusLine of(Version version, StatusCode statusCode) {
		return new HttpStatusLine(version, statusCode);
	}

	public String generateStatusLine() {
		return String.join(GAP, version.getVersion(), statusCode.toResponseString(), "");
	}
}
