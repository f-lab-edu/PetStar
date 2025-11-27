package com.petstarproject.petstar.repository;

import com.petstarproject.petstar.entity.Pet;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository{

    void save(Pet pet);

    Optional<Pet> findById(String id);

    void updateById(String id, Pet pet);

    void delete(Pet pet);
}
