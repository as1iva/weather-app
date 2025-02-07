package org.as1iva.config;

import org.as1iva.interceptor.AuthUserValidationInterceptor;
import org.as1iva.interceptor.NotAuthUserValidationInterceptor;
import org.as1iva.interceptor.SessionValidationInterceptor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("dev")
@ComponentScan("org.as1iva")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final NotAuthUserValidationInterceptor notAuthUserValidationInterceptor;

    private final AuthUserValidationInterceptor authUserValidationInterceptor;

    private final SessionValidationInterceptor sessionValidationInterceptor;

    private final ApplicationContext applicationContext;

    private final Environment env;

    @Autowired
    public WebConfig(@Lazy AuthUserValidationInterceptor authUserValidationInterceptor,
                     @Lazy NotAuthUserValidationInterceptor notAuthUserValidationInterceptor,
                     @Lazy SessionValidationInterceptor sessionValidationInterceptor,
                     ApplicationContext applicationContext,
                     Environment env) {

        this.authUserValidationInterceptor = authUserValidationInterceptor;
        this.notAuthUserValidationInterceptor = notAuthUserValidationInterceptor;
        this.sessionValidationInterceptor = sessionValidationInterceptor;
        this.applicationContext = applicationContext;
        this.env = env;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);

        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(1);

        return viewResolver;
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
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
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

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://api.openweathermap.org")
                .build();
    }
}
