package es.uji.ei1027.ovi;

import es.uji.ei1027.ovi.config.AuthInterceptor;
import es.uji.ei1027.ovi.config.UriInterceptor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
public class OVIConfiguration implements WebMvcConfigurer {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UriInterceptor());

        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns(
                        "/papPati/**",
                        "/oviUser/**",
                        "/admin/**",
                        "/instructor/**"
                )
                .excludePathPatterns(
                        "/papPati/register",
                        "/oviUser/register",
                        "/registerSuccess"
                );
    }
}
