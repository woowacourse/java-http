package org.apache.catalina;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public interface Handler {

	void service(Request request, Response response);
}
