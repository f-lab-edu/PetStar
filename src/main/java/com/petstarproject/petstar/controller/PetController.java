package com.petstarproject.petstar.controller;

import com.petstarproject.petstar.dto.MessageResponse;
import com.petstarproject.petstar.dto.PetInfoResponse;
import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.dto.RegisterRequestJsonMapper;
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
    private final RegisterRequestJsonMapper registerRequestJsonMapper;

    @Autowired
    public PetController(PetService petServiceImpl, RegisterRequestJsonMapper registerRequestJsonMapper) {
        this.petService = petServiceImpl;
        this.registerRequestJsonMapper = registerRequestJsonMapper;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPet(@PathVariable String id) {
        Pet pet = petService.getPet(id);
        return ResponseEntity.ok(PetInfoResponse.from(pet));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerPet(@RequestPart("data") String request,
                                         @RequestPart("image") MultipartFile image) {
        // form data로 받아야해서 "data" 부분을 plainText로 인식해 json mapper 추가
        RegisterRequest registerRequest = registerRequestJsonMapper.fromJson(request);
        // TODO: 인증 기능 연동 후 실제 로그인 유저 ID 사용
        petService.registerPet(registerRequest, image, "dummyId");
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("동물 등록 성공"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePet(@PathVariable String id,
                                       @RequestPart("data") String request,
                                       @RequestPart("image") MultipartFile image) {
        RegisterRequest registerRequest = registerRequestJsonMapper.fromJson(request);
        petService.updatePet(id, registerRequest, image);
        return ResponseEntity.ok(new MessageResponse("동물 정보 수정 완료"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable String id) {
        petService.deletePet(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
