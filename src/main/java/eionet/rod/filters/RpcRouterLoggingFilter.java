package eionet.rod.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RpcRouterLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRouterLoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String method = httpRequest.getMethod();
            String remoteAddr = httpRequest.getRemoteAddr();
            String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
            String userAgent = httpRequest.getHeader("User-Agent");

            LOGGER.info("XML-RPC call: method={}, remoteAddr={}, X-Forwarded-For={}, userAgent={}",
                    method,
                    remoteAddr,
                    xForwardedFor,
                    userAgent);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
