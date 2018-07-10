package com.viewfin.match.core.conf;

import com.viewfin.match.core.enums.SystemDoorEnum;
import com.viewfin.match.core.component.spring.SpringContextSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Auther: pangzhiwang
 * @Date: 2018/6/29 14:21
 * @Description:
 */

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionHandlerInterceptor()).addPathPatterns("/**");
    }

    class SessionHandlerInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            ApplicationContext applicationContext=SpringContextSupport.getApplicationContext();

            RedisTemplate<String, String> redisTemplate=applicationContext.getBean("redisTemplate",RedisTemplate.class);

            OrderConfig orderConfig= applicationContext.getBean(OrderConfig.class);
            String doorMarket=orderConfig.getDoorMarket();

            String flg=redisTemplate.opsForValue().get(doorMarket);

            if(SystemDoorEnum.close.getCode().equals(flg)){
                return false;
            }
            return true;
        }
    }


}
