package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, String> {
    Optional<Code> findByCodeName(String codeName);
}
