package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.global.logger.RequestResponseLoggingInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestResponseLoggingInterceptor requestResponseLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestResponseLoggingInterceptor)
                .order(-100)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/favicon.ico",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}
