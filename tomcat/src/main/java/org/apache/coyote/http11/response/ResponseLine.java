package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.VersionOfProtocol;

public class ResponseLine {
	private VersionOfProtocol versionOfProtocol;
	private StatusCode statusCode;
	private StatusMessage statusMessage;

	public ResponseLine(VersionOfProtocol versionOfProtocol, StatusCode statusCode, StatusMessage statusMessage) {
		this.versionOfProtocol = versionOfProtocol;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public VersionOfProtocol getVersionOfProtocol() {
		return versionOfProtocol;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	public StatusMessage getStatusMessage() {
		return statusMessage;
	}


	public void setVersionOfProtocol(VersionOfProtocol versionOfProtocol) {
		this.versionOfProtocol = versionOfProtocol;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusMessage(StatusMessage statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String generatePlainText() {
		return String.format("%s %d %s \r\n", versionOfProtocol.value(), statusCode.value(), statusMessage.value());
	}
}
