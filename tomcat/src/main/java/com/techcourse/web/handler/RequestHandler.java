package com.techcourse.web.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.coyote.http11.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.web.HttpResponse;
import com.techcourse.web.HttpStatusCode;
import com.techcourse.web.annotation.Query;
import com.techcourse.web.annotation.Request;

public abstract class RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	public HttpResponse handle(HttpRequest request) {
		Method handlerMethod = Arrays.stream(getClass().getDeclaredMethods())
			.filter(method -> method.isAnnotationPresent(Request.class))
			.filter(method -> {
				Request requestAnnotation = method.getAnnotation(Request.class);
				boolean isSameMethod = requestAnnotation.method().equals(request.getMethod());

				String path = requestAnnotation.path();
				boolean isSamePath = path.equals("*") || path.equals(request.getRequestPath());

				return isSameMethod && isSamePath;
			})
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(
				"handler method not found: " + request.getMethod() + " " + request.getRequestPath()));

		if (handlerMethod.isAnnotationPresent(Query.class) && hasNotRequiredQueryParams(handlerMethod, request)) {
			return HttpResponse.builder()
				.protocol(request.getProtocol())
				.statusCode(HttpStatusCode.BAD_REQUEST)
				.build();
		}

		try {
			handlerMethod.setAccessible(true);
			return (HttpResponse)handlerMethod.invoke(this, request);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error(e.getMessage(), e);
			return HttpResponse.builder()
				.protocol(request.getProtocol())
				.statusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
				.build();
		}
	}

	private boolean hasNotRequiredQueryParams(Method handlerMethod, HttpRequest request) {
		Query annotation = handlerMethod.getAnnotation(Query.class);
		return Arrays.stream(annotation.params())
			.anyMatch(param -> param.required() && !request.getRequestQuery().containsKey(param.key()));
	}
}
