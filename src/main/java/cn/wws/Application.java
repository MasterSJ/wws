  package cn.wws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cn.wws.interceptor.BaseInterceptor;
import cn.wws.interceptor.LoginInterceptor;

@SpringBootApplication
@EnableTransactionManagement
public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {  
        SpringApplication.run(Application.class, args);  
    }  
    
    protected SpringApplicationBuilder configure( 
            SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    
    
    /**
     * 设置session过期时间
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
           return new EmbeddedServletContainerCustomizer() {
               @Override
               public void customize(ConfigurableEmbeddedServletContainer container) {
                    container.setSessionTimeout(1500);//单位为S
              }
        };
    }
    
    //mvc控制器
    @Configuration
    static class WebMvcConfigurer extends WebMvcConfigurerAdapter{
        //增加拦截器
        public void addInterceptors(InterceptorRegistry registry){
        	/**首先添加基础拦截器*/
            registry.addInterceptor(new BaseInterceptor()).addPathPatterns("/**");
            /**然后再添加登录拦截器*/
            registry.addInterceptor(new LoginInterceptor())    //指定拦截器类
                    .addPathPatterns("/**")        //指定该类拦截的url
            		.excludePathPatterns("/")
            		.excludePathPatterns("/main")
            		.excludePathPatterns("/showSignin")
            		.excludePathPatterns("/showBlog")
                    .excludePathPatterns("/blog/info/**")
                    .excludePathPatterns("/likeFlag/**")
            		.excludePathPatterns("/doWriteBlog")
            		.excludePathPatterns("/showMessageBoard")
            		.excludePathPatterns("/showNewMessage")
            		.excludePathPatterns("/writeMessage")
            		.excludePathPatterns("/showRegister")
            		.excludePathPatterns("/doRegister")
            		.excludePathPatterns("/showGrabPseudonym")
            		.excludePathPatterns("/grabPseudonym")
            		.excludePathPatterns("/doSignin")
            		.excludePathPatterns("/error")
            		.excludePathPatterns("/logOut")
            		.excludePathPatterns("/showProfile")
                    .excludePathPatterns("/allow/*")
                    .excludePathPatterns("/wechat/*");
        }
    }
}
