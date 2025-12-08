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
    private final FileStorageService fileStorageService;

    @Autowired
    public PetServiceImpl(PetRepository petRepository, FileStorageService fileStorageService) {
        this.petRepository = petRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Pet getPet(String id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found: " + id));
    }

    @Transactional
    @Override
    public void registerPet(RegisterRequest request, MultipartFile image, String userId) {
        String petId = UUID.randomUUID().toString();

        String profileImageKey = null; // todo: 디폴트 이미지?

        if (image != null && !image.isEmpty()) {
            String imageKey = String.format(
                    "pets/%s/profile/%s",
                    petId,
                    UUID.randomUUID()
            );

            profileImageKey = fileStorageService.upload(image, imageKey);
        }

        Pet pet = new Pet(
                petId,
                userId,
                request.getName(),
                request.getAge(),
                request.getSpecies(),
                request.getGender(),
                request.getBio(),
                profileImageKey,
                0
        );
        petRepository.save(pet);
    }

    @Transactional
    @Override
    public void updatePet(String id, RegisterRequest request, MultipartFile image) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found: " + id));

        if (image != null && !image.isEmpty()) {
            String imageKey = String.format(
                    "pets/%s/profile/%s",
                    pet.getId(),
                    UUID.randomUUID()
            );

            String storedKey = fileStorageService.upload(image, imageKey);
            pet.setProfileImageKey(storedKey);

            // option: 이전 이미지 삭제?
        }

        pet.setName(request.getName());
        pet.setAge(request.getAge());
        pet.setSpecies(request.getSpecies());
        pet.setGender(request.getGender());
        pet.setBio(request.getBio());
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
