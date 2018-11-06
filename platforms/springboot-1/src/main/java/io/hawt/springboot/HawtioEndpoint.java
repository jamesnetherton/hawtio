package io.hawt.springboot;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.actuate.endpoint.mvc.AbstractNamedMvcEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Spring Boot endpoint to expose hawtio.
 */
@ConfigurationProperties(prefix = "endpoints.hawtio", ignoreUnknownFields = false)
public class HawtioEndpoint extends AbstractNamedMvcEndpoint {

    private final EndpointPathResolver resolver;

    private List<HawtPlugin> plugins;

    public HawtioEndpoint(EndpointPathResolver resolver) {
        super("hawtio", "/hawtio", true);
        this.resolver = resolver;
    }

    public void setPlugins(final List<HawtPlugin> plugins) {
        this.plugins = plugins;
    }

    @RequestMapping(value = {"", "/", "**/{path:^(?:(?!\\bjolokia\\b|\\.).)*$}"}, produces = MediaType.TEXT_HTML_VALUE)
    public String redirectToIndexPage(final HttpServletRequest request) {
        return getIndexHtmlRedirect(request);
    }

    @RequestMapping("/plugin")
    @ResponseBody
    public List<HawtPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry // @formatter:off
            .addResourceHandler(resolver.resolveUrlMapping(getPath(), "/plugins/**"))
            .addResourceLocations(
                "/app/",
                "classpath:/hawtio-static/app/");
        registry
            .addResourceHandler(resolver.resolveUrlMapping(getPath(), "/**"))
            .addResourceLocations(
                "/",
                "/app/",
                "classpath:/hawtio-static/",
                "classpath:/hawtio-static/app/");
        registry
            .addResourceHandler("/img/**")
            .addResourceLocations("classpath:/hawtio-static/img/"); // @formatter:on
    }

    protected String getIndexHtmlRedirect(final HttpServletRequest request) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.newInstance();
        builder.path(resolver.resolve(getPath()));
        builder.path("/index.html");
        builder.query(request.getQueryString());
        UriComponents uriComponents = builder.build();
        String path = uriComponents.getPath();
        return "forward:" + path;
    }
}
