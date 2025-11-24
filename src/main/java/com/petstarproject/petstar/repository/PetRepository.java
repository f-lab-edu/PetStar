package com.petstarproject.petstar.repository;

import entity.Pet;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository {

    void save(Pet pet);

    Pet searchById(String id);

    void updateById(String id, Pet pet);

    void deleteById(String id);
}
