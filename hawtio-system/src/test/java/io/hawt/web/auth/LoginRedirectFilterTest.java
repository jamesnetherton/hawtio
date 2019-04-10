package io.hawt.web.auth;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import static io.hawt.web.auth.LoginRedirectFilter.PARAM_PATH_PREFIX;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoginRedirectFilterTest {

    private LoginRedirectFilter loginRedirectFilter;
    private FilterConfig filterConfig;
    private ServletContext servletContext;

    @Before
    public void setUp() {
        filterConfig = mock(FilterConfig.class);
        servletContext = mock(ServletContext.class);
    }

    @Test
    public void shouldTestSecuredPaths() throws Exception {
        loginRedirectFilter = new LoginRedirectFilter();
        when(filterConfig.getServletContext()).thenReturn(servletContext);
        loginRedirectFilter.init(filterConfig);
        assertTrue(loginRedirectFilter.isSecuredPath("/d"));
        assertTrue(loginRedirectFilter.isSecuredPath("/e/f"));
        assertFalse(loginRedirectFilter.isSecuredPath("/auth/login"));
        assertFalse(loginRedirectFilter.isSecuredPath("/auth/logout"));
        assertFalse(loginRedirectFilter.isSecuredPath("/img/test.jpg"));
    }

    @Test
    public void customizedUnsecuredPaths() throws Exception {
        loginRedirectFilter = new LoginRedirectFilter();
        when(filterConfig.getServletContext()).thenReturn(servletContext);
        when(filterConfig.getInitParameter(PARAM_PATH_PREFIX)).thenReturn("/foo/bar");
        loginRedirectFilter.init(filterConfig);
        assertTrue(loginRedirectFilter.isSecuredPath("/a"));
        assertTrue(loginRedirectFilter.isSecuredPath("/b/c"));
        assertTrue(loginRedirectFilter.isSecuredPath("/foo/bar/d"));
        assertTrue(loginRedirectFilter.isSecuredPath("/foo/bar/e/f"));
        assertFalse(loginRedirectFilter.isSecuredPath("/foo/bar/auth/login"));
        assertFalse(loginRedirectFilter.isSecuredPath("/foo/bar/auth/logout"));
        assertFalse(loginRedirectFilter.isSecuredPath("/foo/bar/img/test.jpg"));
    }
}
