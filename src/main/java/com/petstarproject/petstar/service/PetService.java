package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.entity.Pet;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

    Pet getPet(String id);

    void registerPet(RegisterRequest request, MultipartFile image, String id);

    void updatePet(String id, RegisterRequest request, MultipartFile image);

    void deletePet(String id);

}
