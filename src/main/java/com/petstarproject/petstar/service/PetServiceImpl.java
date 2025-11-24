package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.repository.PetRepository;
import entity.Pet;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return petRepository.searchById(id);
    }

    @Override
    public void registerPet(RegisterRequest request, MultipartFile image, User user) {
        UUID UUid = UUID.randomUUID();
        String id = UUid.toString();
        Pet pet = new Pet(id, user.getId(), request.getName(), request.getAge(),
                request.getSpecies(), request.getGender(), request.getBio(), "s3_image_key", 0);
        petRepository.save(pet);
    }

    @Override
    public void updatePet(String id, RegisterRequest request, MultipartFile image) {
        Pet pet = petRepository.searchById(id);
        pet.setName(request.getName());
        pet.setAge(request.getAge());
        pet.setSpecies(request.getSpecies());
        pet.setGender(request.getGender());
        pet.setBio(request.getBio());
        petRepository.updateById(id, pet);
    }


    @Override
    public void deletePet(String id) {
        petRepository.deleteById(id);
    }
}
