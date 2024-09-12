package com.techcourse.web.util;

import java.util.UUID;

public class JsessionIdGenerator {

	public static String generate() {
		return UUID.randomUUID().toString();
	}
}
