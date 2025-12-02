package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.repository.PetRepository;
import com.petstarproject.petstar.entity.Pet;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    @Autowired
    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet getPet(String id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found: " + id));
    }

    @Transactional
    @Override
    public void registerPet(RegisterRequest request, MultipartFile image, String userId) {
        UUID UUid = UUID.randomUUID();
        String id = UUid.toString();
        Pet pet = new Pet(id, userId, request.getName(), request.getAge(),
                request.getSpecies(), request.getGender(), request.getBio(), "s3_image_key", 0);
        petRepository.save(pet);
    }

    @Transactional
    @Override
    public void updatePet(String id, RegisterRequest request, MultipartFile image) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pet not found: " + id));
        pet.setName(request.getName());
        pet.setAge(request.getAge());
        pet.setSpecies(request.getSpecies());
        pet.setGender(request.getGender());
        pet.setBio(request.getBio());
//        jpa에서 find 후 setter만 호출되어도 자동으로 update되어서 필요 없어짐
//        petRepository.updateById(id, pet);
    }

    @Transactional
    @Override
    public void deletePet(String id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found:" + id));
        // TODO: 검증로직
        petRepository.delete(pet);
    }
}
