package com.petstarproject.petstar.controller;

import com.petstarproject.petstar.dto.PetInfoResponse;
import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.service.PetService;
import com.petstarproject.petstar.entity.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petServiceImpl) {
        this.petService = petServiceImpl;
    }


    @GetMapping("/pet/{id}")
    public ResponseEntity<?> getPet(@PathVariable String id) {
        Pet pet = petService.getPet(id);
        PetInfoResponse res = new PetInfoResponse(pet.getId(), pet.getProfileImageKey(), pet.getName(), pet.getAge(), pet.getSpecies(), pet.getGender(), pet.getBio());
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/pet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerPet(@RequestPart("data") RegisterRequest request,
                                         @RequestPart("image") MultipartFile image) {

        return ResponseEntity.ok("동물 등록 성공:");
    }

    @PutMapping(value = "/pet/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePet(@PathVariable String id,
                                       @RequestPart RegisterRequest request,
                                       @RequestPart MultipartFile image) {
        return ResponseEntity.ok("정보 수정 완료");
    }

    @DeleteMapping("/pet/{id}")
    public ResponseEntity<?> deletePet(@PathVariable String id) {
        return ResponseEntity.ok("동물 삭제 완료");
    }
}
