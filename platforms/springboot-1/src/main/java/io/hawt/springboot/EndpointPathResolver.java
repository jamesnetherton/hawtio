package io.hawt.springboot;

import io.hawt.util.Strings;

import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;

public class EndpointPathResolver {

    private final ServerProperties serverProperties;
    private final ManagementServerProperties managementServerProperties;

    public EndpointPathResolver(final ServerProperties serverProperties,
                                final ManagementServerProperties managementServerProperties) {
        this.serverProperties = serverProperties;
        this.managementServerProperties = managementServerProperties;
    }

    public String resolve(String endpointBasePath) {
        final String servletPrefix = resolveServletPrefix();
        final String managementContextPath = managementServerProperties.getContextPath();
        final String endpointPath = Strings.webContextPath(servletPrefix, managementContextPath, endpointBasePath);
        return endpointPath.isEmpty() ? "/" : endpointPath;
    }

    public String resolveUrlMapping(String endpointBasePath, String... mappings) {
        final String servletPrefix = resolveServletPrefix();
        String endpointPath = resolve(endpointBasePath);

        if (!servletPrefix.equals("/")) {
            endpointPath = endpointPath.replace(servletPrefix, "");
        }

        return Strings.webContextPath(endpointPath, mappings);
    }

    private String resolveServletPrefix() {
        final Integer serverPort = serverProperties.getPort() != null ? serverProperties.getPort() : 8080;
        final Integer managementPort = managementServerProperties.getPort() != null ? managementServerProperties.getPort() : serverPort;
        String servletPrefix = "";
        if (serverPort.equals(managementPort)) {
            servletPrefix = serverProperties.getServletPrefix();
        }
        return servletPrefix;
    }
}
