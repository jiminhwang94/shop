package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
// extends WebSecurityConfigurerAdapter이 2.7부터 이 상속은 사용을 안한다. 그래서 시큐리티 부분을 수정했다.
//로그인 성공 테스트 오류가 시큐리티 부분의 문제였다. 왜? 위의 상속이 이제 적용이 안되서 @Bean 객체를 만들어야했다.
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    //WebSecurityConfigurationAdapter는 이제 사장되어서 override하여 SecurityFilterChain을 사용할 수 없다
    //따라서 직접 SecurityFilterChain을 Bean으로 설정하여 밑에 필요한 부분을 커스터마이징 하면 된다.
    ///
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*http
                // "/thymeleaf/**"로 시작하는 모든 경로에 대해 인증 없이 접근을 허용합니다.
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/thymeleaf/**").permitAll()
                        // 그 외의 모든 요청에 대해 인증이 필요합니다.
                        .anyRequest().authenticated()
                );*/
        /*http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());*/

        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/");

        http.authorizeRequests()
                .mvcMatchers("/","/members/**",
                        "/item/**","/images/**").permitAll()        //permitAll()을 통해 모든 사용자가 인증없이 해당 경로에 접근할 수 있도록 설정한다. 많은 경로가 이에 해당.
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();  //mvcMatchers에 설정한 경로를 제외한 나머지 경로들은 모두 인증을 요구하도록 설정.

        http.exceptionHandling()
                .authenticationEntryPoint(
                        new CustomAuthenticationEntryPoint());      //인증되지 않은 사용자가 리소스에 접근할때 수행되는 핸들러

        return http.build();
    }

    //WebSecurity를 커스텀 설정하기 위해서 WebSecurityCustomizer라는 콜백 인터페이스를 사용합니다.
    // 디렉터리의 하위 파일은 인증을 무시하도록 설정.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

//    AuthenticationManagerBuilder가 AuthenticationManager를 생성한다.
//    AuthenticationManager에서 인증이 이루어진다.
/*스프링 시큐리티의 인증을 담당하는 AuthenticationManager는 이전 설정 방법으로
authenticationManagerBuilder를 이용해서 userDetailsService와 passwordEncoder를 설정해주어야 했습니다.
그러나 변경된 설정에서는 AuthenticationManager 빈 생성 시 스프링의 내부 동작으로 인해
위에서 작성한 UserSecurityService와 PasswordEncoder가 자동으로 설정됩니다.*/
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


//    BCryptPasswordEncoder의 해시 함수를 이용하여 비밀번호 암호화하여 저장.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }


}
