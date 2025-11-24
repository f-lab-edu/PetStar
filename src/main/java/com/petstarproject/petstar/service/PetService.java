package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.RegisterRequest;
import entity.Pet;
import entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

    /***
     * todo
     *  1. 펫 을 등록하는 api 로직 작성
     *  2. 펫 정보를 수정하는 api 로직 작성
     *  3. 펫 정보를 삭제하는 api 로직 작성
     *  4. 펫 정보를 조회하는 api 로직 작성
     */

    Pet getPet(String id);

    void registerPet(RegisterRequest request, MultipartFile image, User user);

    void updatePet(String id, RegisterRequest request, MultipartFile image);

    void deletePet(String id);

}
