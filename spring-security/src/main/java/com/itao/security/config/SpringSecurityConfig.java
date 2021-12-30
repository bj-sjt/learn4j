package com.itao.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**"); // 放行静态资源
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                // 登录页面
                .loginPage("/login")
                // 指定表单中 密码输入框的name属性的值 默认 password
                .passwordParameter("pass")
                // 指定表单中 用户名输入框的name属性的值 默认 username
                .usernameParameter("user")
                // 表单跳转的地址
                .loginProcessingUrl("/user/login")
                // 登录成功后默认的跳转页面
                .defaultSuccessUrl("/index")
                .and()
                .logout()
                // 登出的地址
                .logoutUrl("/logout")
                // 登出后跳转的地址
                .logoutSuccessUrl("/index")
                .and()
                .authorizeRequests()
                // 放行的地址
                .antMatchers("/", "/index", "/user/login", "/login")
                .permitAll()
                .anyRequest()
                .authenticated()
                /*.and()
                .csrf()
                //关闭 csrf 默认开启
                .disable()*/;
    }


    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            System.out.println("username :" + username);
            List<GrantedAuthority> authorities =
                    AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_user");
            return new User("user", passwordEncoder().encode("123"), authorities);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
