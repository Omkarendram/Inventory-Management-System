package com.inventory.repository;

import com.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(String userId);

    User findByUsername(String username);

    boolean existsByUserId(String userId);

    boolean existsByUsername(String username);

    boolean existsByUserIdAndIdNot(String userId, Long id);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
