package org.as1iva.config;

import org.as1iva.interceptor.AuthUserValidationInterceptor;
import org.as1iva.interceptor.NotAuthUserValidationInterceptor;
import org.as1iva.interceptor.SessionValidationInterceptor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("test")
@ComponentScan("org.as1iva")
@PropertySource("classpath:application-test.properties")
@EnableTransactionManagement
public class TestConfig implements WebMvcConfigurer {

    private final NotAuthUserValidationInterceptor notAuthUserValidationInterceptor;

    private final AuthUserValidationInterceptor authUserValidationInterceptor;

    private final SessionValidationInterceptor sessionValidationInterceptor;

    private final WebApplicationContext webApplicationContext;

    private final Environment env;

    @Autowired
    public TestConfig(@Lazy AuthUserValidationInterceptor authUserValidationInterceptor,
                      @Lazy NotAuthUserValidationInterceptor notAuthUserValidationInterceptor,
                      @Lazy SessionValidationInterceptor sessionValidationInterceptor,
                      WebApplicationContext webApplicationContext,
                      Environment env) {

        this.authUserValidationInterceptor = authUserValidationInterceptor;
        this.notAuthUserValidationInterceptor = notAuthUserValidationInterceptor;
        this.sessionValidationInterceptor = sessionValidationInterceptor;
        this.webApplicationContext = webApplicationContext;
        this.env = env;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("database.driver"));
        dataSource.setUrl(env.getRequiredProperty("database.url"));
        dataSource.setUsername(env.getRequiredProperty("database.username"));
        dataSource.setPassword(env.getRequiredProperty("database.password"));

        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        properties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));

        return properties;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("org.as1iva.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();

        transactionManager.setSessionFactory(sessionFactory().getObject());

        return transactionManager;
    }

    @Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource())
                .locations("classpath:db/test_migration")
                .load();

        flyway.migrate();

        return flyway;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notAuthUserValidationInterceptor)
                .addPathPatterns("/")
                .excludePathPatterns("/login", "/registration");

        registry.addInterceptor(authUserValidationInterceptor)
                .addPathPatterns("/login", "/registration");

        registry.addInterceptor(sessionValidationInterceptor)
                .addPathPatterns("/");
    }
}
