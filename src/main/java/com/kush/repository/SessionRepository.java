package com.kush.repository;


import com.kush.entity.Session;
import com.kush.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByToken(String token);

    List<Session> findByCreatedBy(User user);
}
