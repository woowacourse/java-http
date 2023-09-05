package cache.com.example.cachecontrol;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class NoCacheInterceptor implements HandlerInterceptor {

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
		final Object handler, @Nullable final ModelAndView modelAndView) throws Exception {
		response.addHeader("Cache-control", "no-cache");
		response.addHeader("Cache-control", "private");
	}
}
