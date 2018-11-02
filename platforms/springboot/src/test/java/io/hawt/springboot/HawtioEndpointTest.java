package io.hawt.springboot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

public class HawtioEndpointTest {

    private HawtioEndpoint hawtioEndpoint;
    private EndpointPathResolver endpointPathResolver;

    @Before
    public void setUp() {
        endpointPathResolver = Mockito.mock(EndpointPathResolver.class);
        hawtioEndpoint = new HawtioEndpoint(endpointPathResolver);
    }

    @Test
    public void testGetIndexHtmlRedirect() {
        Mockito.when(endpointPathResolver.resolve("hawtio")).thenReturn("/hawtio");

        runTestGetIndexHtmlRedirect(null, null,
            "forward:/hawtio/index.html");
        runTestGetIndexHtmlRedirect("", "",
            "forward:/hawtio/index.html");
        runTestGetIndexHtmlRedirect("/hawtio", null,
            "forward:/hawtio/index.html");
        runTestGetIndexHtmlRedirect("/hawtio/", null,
            "forward:/hawtio/index.html");
        runTestGetIndexHtmlRedirect("/hawtio/jmx/attributes", "?nid=root-java.nio",
            "forward:/hawtio/index.html");
    }

    private void runTestGetIndexHtmlRedirect(String requestURI, String queryString, String expectedResult) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (requestURI != null) {
            request.setRequestURI(requestURI);
        }
        if (queryString != null) {
            request.setQueryString(queryString);
        }

        String result = hawtioEndpoint.getIndexHtmlRedirect(request);

        assertEquals(expectedResult, result);
    }

}
