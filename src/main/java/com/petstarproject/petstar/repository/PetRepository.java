package com.petstarproject.petstar.repository;

import com.petstarproject.petstar.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {
//    jpa 기본제공 메서드
//
//    void save(Pet pet);
//
//    Optional<Pet> findById(String id);
//
//    void updateById(String id, Pet pet);
//
//    void delete(Pet pet);
}
