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

        String profileImageKey = uploadProfileImageIfPresent(image, petId); // todo: key 값이 null일때 클라이언트에서 디폴트 이미지 불러오기

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

        String profileImageKey = uploadProfileImageIfPresent(image, pet.getId());
        if (profileImageKey != null) {
            pet.setProfileImageKey(profileImageKey);
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
        // TODO: User 기능 추가 후 검증로직 추가
        petRepository.delete(pet);
    }

    /**
     * 이미지가 empty가 아니면 저장하고 key를 반환 하고 없으면 null을 반환합니다.
     * @param image 클라이언트로 부터 받은 이미지 파일
     * @param petId 프로필 이미지를 업로드할 펫의 ID
     * @return 저장된 이미지의 key
     */
    private String uploadProfileImageIfPresent(MultipartFile image, String petId) {
        if (image != null && !image.isEmpty()) {
            String imageKey = String.format(
                    "pets/%s/profile/%s",
                    petId,
                    UUID.randomUUID()
            );

            return fileStorageService.upload(image, imageKey);
        }

        return null;
    }
}
