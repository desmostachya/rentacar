package com.kush.repository;

import com.kush.entity.User;
import com.kush.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    List<User> findByRole(UserRole role);
    List<User> findByIsActive(Boolean isActive);
}
