package io.hawt.web.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * Redirect to login page when authentication is enabled.
 */
public class LoginRedirectFilter implements Filter {

    public static final String PARAM_PATH_PREFIX = "pathPrefix";

    private static final Logger LOG = LoggerFactory.getLogger(LoginRedirectFilter.class);

    private AuthenticationConfiguration authConfiguration;

    private List<String> unsecuredPaths = Collections.emptyList();

    private Redirector redirector = new Redirector();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        authConfiguration = AuthenticationConfiguration.getConfiguration(filterConfig.getServletContext());

        InputStream resource = loadResource("/resourceList.txt");
        if (resource != null) {
            try {
                unsecuredPaths = Collections.unmodifiableList(IOUtils.readLines(resource));

                String pathPrefix = filterConfig.getInitParameter(PARAM_PATH_PREFIX);
                if (pathPrefix != null) {
                    unsecuredPaths = unsecuredPaths.stream()
                        .map(path -> pathPrefix + path)
                        .collect(collectingAndThen(toList(), Collections::unmodifiableList));
                }
            } catch (IOException e) {
                LOG.error("Failed reading resourceList.txt. Authentication may not work correctly.", e);
            }
        } else {
            LOG.error("Failed loading resourceList.txt. Authentication may not work correctly.");
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String path = httpRequest.getServletPath();

        if (authConfiguration.isEnabled() && !authConfiguration.isKeycloakEnabled()
            && !isAuthenticated(session) && isSecuredPath(path)) {
            redirector.doRedirect(httpRequest, httpResponse, AuthenticationConfiguration.LOGIN_URL);
        } else {
            chain.doFilter(request, response);
        }
    }

    boolean isSecuredPath(String path) {
        return !unsecuredPaths.contains(path);
    }

    @Override
    public void destroy() {
    }

    public void setRedirector(Redirector redirector) {
        this.redirector = redirector;
    }

    private boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("subject") != null;
    }

    private InputStream loadResource(String resource) {
        InputStream in = null;
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != null) {
            in = tccl.getResourceAsStream(resource);
        }

        if (in == null) {
            in = LoginRedirectFilter.class.getClassLoader().getResourceAsStream(resource);
        }

        if (in == null) {
            in = LoginRedirectFilter.class.getResourceAsStream(resource);
        }

        return in;
    }
}
