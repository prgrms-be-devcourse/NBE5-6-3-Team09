package com.codemap.core.user.repository;

import com.codemap.core.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email); // 이메일 중복 확인
    Optional<User> findByEmail(String email); // 로그인용
    List<User> findAllByRole(String roleUser);
}