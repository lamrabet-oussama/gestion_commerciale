package com.moonsystem.gestion_commerciale.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moonsystem.gestion_commerciale.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByCod(Integer id);

    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);

    List<User> findAll();
    // @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    // Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
