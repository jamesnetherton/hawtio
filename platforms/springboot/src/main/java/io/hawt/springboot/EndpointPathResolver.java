package io.hawt.springboot;

import io.hawt.util.Strings;

import java.util.Map;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;

public class EndpointPathResolver {

    private final WebEndpointProperties webEndpointProperties;
    private final ServerProperties serverProperties;
    private final ManagementServerProperties managementServerProperties;

    public EndpointPathResolver(final WebEndpointProperties webEndpointProperties,
                                final ServerProperties serverProperties,
                                final ManagementServerProperties managementServerProperties) {
        this.webEndpointProperties = webEndpointProperties;
        this.serverProperties = serverProperties;
        this.managementServerProperties = managementServerProperties;
    }

    public String resolve(final String endpointName) {
        final Map<String, String> pathMapping = webEndpointProperties.getPathMapping();
        final String basePath = webEndpointProperties.getBasePath();
        final String servletPath = serverProperties.getServlet().getPath();
        String endpointPathMapping = pathMapping.get(endpointName);

        if (endpointPathMapping == null) {
            endpointPathMapping = endpointName;
        }

        final String webContextPath = Strings.webContextPath(servletPath, basePath, endpointPathMapping);
        return webContextPath.isEmpty() ? "/" : webContextPath;
    }

    public String resolveUrlMapping(String endpointName, String... mappings) {
        String servletPath = serverProperties.getServlet().getPath();
        String endpointPath = resolve(endpointName);

        if (!servletPath.equals("/")) {
            endpointPath = endpointPath.replace(servletPath, "");
        }

        return Strings.webContextPath(endpointPath, mappings);
    }
}
