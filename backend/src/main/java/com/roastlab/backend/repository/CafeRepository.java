package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeRepository extends JpaRepository<Cafe, Integer> {
}