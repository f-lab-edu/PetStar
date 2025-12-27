package com.petstarproject.petstar.controller;

import com.petstarproject.petstar.dto.MessageResponse;
import com.petstarproject.petstar.dto.PetInfoResponse;
import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.service.PetService;
import com.petstarproject.petstar.entity.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/pets")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petServiceImpl) {
        this.petService = petServiceImpl;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPet(@PathVariable String id) {
        Pet pet = petService.getPet(id);
        return ResponseEntity.ok(PetInfoResponse.from(pet));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerPet(@RequestPart("data") RegisterRequest request,
                                         @RequestPart("image") MultipartFile image) {

        // TODO: 인증 기능 연동 후 실제 로그인 유저 ID 사용
        petService.registerPet(request, image, "dummyId");
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("동물 등록 성공"));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePet(@PathVariable String id,
                                       @RequestPart("data") RegisterRequest request,
                                       @RequestPart("image") MultipartFile image) {
        petService.updatePet(id, request, image);
        return ResponseEntity.ok(new MessageResponse("동물 정보 수정 완료"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable String id) {
        petService.deletePet(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
