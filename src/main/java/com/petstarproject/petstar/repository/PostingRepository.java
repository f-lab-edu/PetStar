package com.petstarproject.petstar.repository;

import com.petstarproject.petstar.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting, String> {
}
