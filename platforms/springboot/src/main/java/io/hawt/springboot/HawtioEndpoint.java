package io.hawt.springboot;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * Spring Boot endpoint to expose hawtio.
 */
@ControllerEndpoint(id = "hawtio")
public class HawtioEndpoint implements WebMvcConfigurer {

    private final EndpointPathResolver endpointResolver;
    private List<HawtPlugin> plugins;

    public HawtioEndpoint(final EndpointPathResolver endpointResolver) {
        this.endpointResolver = endpointResolver;
    }

    public void setPlugins(final List<HawtPlugin> plugins) {
        this.plugins = plugins;
    }

    @RequestMapping(value = {"/", "**/{path:^(?:(?!\\bjolokia\\b|\\.).)*$}"}, produces = MediaType.TEXT_HTML_VALUE)
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
            .addResourceHandler(endpointResolver.resolveUrlMapping("hawtio", "/plugins/**"))
            .addResourceLocations(
                "/app/",
                "classpath:/hawtio-static/app/");
        registry
            .addResourceHandler(endpointResolver.resolveUrlMapping("hawtio", "/**"))
            .addResourceLocations(
                "/",
                "/app/",
                "classpath:/hawtio-static/",
                "classpath:/hawtio-static/app/");
        registry
            .addResourceHandler(endpointResolver.resolveUrlMapping("hawtio", "/img/**"))
            .addResourceLocations("classpath:/hawtio-static/img/"); // @formatter:on
    }

    protected String getIndexHtmlRedirect(final HttpServletRequest request) {
        final String endpointPath = endpointResolver.resolve("hawtio");
        final UriComponents uriComponents = ServletUriComponentsBuilder
            .fromPath(endpointPath)
            .path("/index.html")
            .query(request.getQueryString())
            .build();
        return "forward:" + uriComponents.getPath();
    }
}
