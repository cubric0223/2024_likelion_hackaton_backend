package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findByName(String schoolName);
}
