package org.apache.coyote.http11.response.header;

import org.apache.coyote.http11.common.HttpMessageConfig;

public enum StatusCode {

	OK(200, "OK")
	;

	private final int code;
	private final String name;

	StatusCode(final int code, final String name) {
		this.code = code;
		this.name = name;
	}

	public String toMessage() {
		return String.join(HttpMessageConfig.WORD_DELIMITER.getValue(), String.valueOf(code), name);
	}
}
