package org.zipper.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 用户信息查询服务
 *
 * @author zhuxj
 * @since 2020/10/21
 */
@Service
public class DaoUserDetailService implements UserDetailsService {

    /**
     * 通过用户名获取用户信息
     *
     * @param s 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        return User.builder()
                .username(s)
                .password(new BCryptPasswordEncoder().encode("123456"))
                .authorities(new ArrayList<>())
                .build();
    }


}
