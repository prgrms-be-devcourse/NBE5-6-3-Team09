package com.codemap.core.infra.auth;

import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("🔐 로그인 시도한 이메일: {}", email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("이메일을 찾을 수 없습니다: " + email));

        log.info("✅ 사용자 정보 찾음: {}", user.getEmail());

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
