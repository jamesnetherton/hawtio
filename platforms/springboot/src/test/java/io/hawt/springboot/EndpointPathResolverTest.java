package io.hawt.springboot;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties.Servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EndpointPathResolverTest {

    private Map<String, String> pathMappings;
    private WebEndpointProperties webEndpointProperties;
    private ServerProperties serverProperties;
    private ManagementServerProperties managementServerProperties;
    private Servlet servlet;
    private EndpointPathResolver resolver;

    @Before
    public void setUp() {
        pathMappings = new HashMap<>();
        pathMappings.put("hawtio", "/hawtio");

        webEndpointProperties = mock(WebEndpointProperties.class);
        serverProperties = mock(ServerProperties.class);
        managementServerProperties = mock(ManagementServerProperties.class);
        servlet = mock(Servlet.class);
        resolver = new EndpointPathResolver(webEndpointProperties, serverProperties, managementServerProperties);
    }

    @Test
    public void testDefaultsToEndpointNameWhenNoMatchingPathMapping() {
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("");
        assertEquals("/hawtio", resolver.resolve("hawtio"));
    }

    @Test
    public void testMatchingPathMapping() {
        when(webEndpointProperties.getPathMapping()).thenReturn(pathMappings);
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("");
        assertEquals("/hawtio", resolver.resolve("hawtio"));
    }

    @Test
    public void testMatchingPathWithWebEndpointBasePath() {
        when(webEndpointProperties.getPathMapping()).thenReturn(pathMappings);
        when(webEndpointProperties.getBasePath()).thenReturn("/base-path");
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("");
        assertEquals("/base-path/hawtio", resolver.resolve("hawtio"));
    }

    @Test
    public void testMatchingPathWithServletPath() {
        when(webEndpointProperties.getPathMapping()).thenReturn(pathMappings);
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("/servlet-path");
        assertEquals("/servlet-path/hawtio", resolver.resolve("hawtio"));
    }

    @Test
    public void testMatchingPathWithWebEndpointBaseAndServletPath() {
        when(webEndpointProperties.getPathMapping()).thenReturn(pathMappings);
        when(webEndpointProperties.getBasePath()).thenReturn("/base-path");
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("/servlet-path");
        assertEquals("/servlet-path/base-path/hawtio", resolver.resolve("hawtio"));
    }

    @Test
    public void testRootPaths() {
        when(webEndpointProperties.getPathMapping()).thenReturn(pathMappings);
        when(webEndpointProperties.getBasePath()).thenReturn("/");
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("/");
        assertEquals("/hawtio", resolver.resolve("hawtio"));
    }

    @Test
    public void testEmptyPaths() {
        pathMappings.put("test", "");
        when(webEndpointProperties.getPathMapping()).thenReturn(pathMappings);
        when(webEndpointProperties.getBasePath()).thenReturn("");
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("");
        assertEquals("/", resolver.resolve("test"));
    }

    @Test
    public void testResolveUrlMapping() {
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("");
        assertEquals("/hawtio/foo/bar/cheese", resolver.resolveUrlMapping("hawtio", "foo", "bar", "cheese"));
    }

    @Test
    public void testResolveUrlMappingWithServletPath() {
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("/servlet-path");
        assertEquals("/hawtio/foo/bar/cheese", resolver.resolveUrlMapping("hawtio", "foo", "bar", "cheese"));
    }

    @Test
    public void testResolveUrlMappingWhereEndpointPathContainsServletPath() {
        pathMappings.put("jolokia", "/servlet-path/jolokia");
        when(serverProperties.getServlet()).thenReturn(servlet);
        when(servlet.getPath()).thenReturn("/servlet-path");
        assertEquals("/jolokia/foo/bar/cheese", resolver.resolveUrlMapping("jolokia", "foo", "bar", "cheese"));
    }
}
