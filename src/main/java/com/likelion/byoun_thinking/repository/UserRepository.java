package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.dto.UserInfoDTO;
import com.likelion.byoun_thinking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer userId);

    @Query("SELECT new com.likelion.byoun_thinking.dto.UserInfoDTO(u.name, u.email, s.name, u.imageUrl) " +
            "FROM User u JOIN u.school s " +
            "WHERE u.userId = :userId")
    List<UserInfoDTO> getUserInfoByUserId(@Param("userId") Integer userId);
}
