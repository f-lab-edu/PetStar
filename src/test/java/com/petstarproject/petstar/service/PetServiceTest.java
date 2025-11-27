package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.entity.Pet;
import com.petstarproject.petstar.entity.User;
import com.petstarproject.petstar.enums.Gender;
import com.petstarproject.petstar.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetServiceImpl petService;

    @Test
    @DisplayName("존재하는 ID로 Pet을 조회하면 Pet을 반환한다")
    void getPet_success() {
        //given
        String id = "test_id";
        Pet pet = new Pet(id, "user1", "강아지", 1, "Dog", Gender.MALE, "어릴때 부터 같이 자란 친구입니다!", "s3_key", 0);

        given(petRepository.findById(id)).willReturn(Optional.of(pet));

        //when
        Pet result = petService.getPet(id);

        //then
        assertThat(result.getId()).isEqualTo(id);
        verify(petRepository).findById(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 Pet 조회 시 EntityNotFoundExeption 발생")
    void getPet_notFound() {
        //given
        String id = "not_exist_id";
        given(petRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> petService.getPet(id));
        verify(petRepository).findById(id);
    }

    @Test
    @DisplayName("Pet 등록 시 Repository save가 호출된다")
    void registerPet_success() {
        //given
        RegisterRequest req = new RegisterRequest("삐삐", 1, "Cat", Gender.FEMALE, "시크한 고양이");
        User user = new User("user1", "test@email.com", "user1", "안녕하세요");
        MultipartFile file = mock(MultipartFile.class);

        //when
        petService.registerPet(req, file, user);

        //then
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    @DisplayName("존재하는 ID로 Pet 수정 시 정보 업데이트 후 저장된다")
    void updatePet_success() {
        //given
        String id = "test_id";
        Pet pet = new Pet(id, "user1", "강아지", 1, "Dog", Gender.MALE, "어릴때 부터 같이 자란 친구입니다!", "s3_key", 0);
        RegisterRequest req = new RegisterRequest("삐삐", 2, "Cat", Gender.FEMALE, "시크한 고양이");
        MultipartFile file = mock(MultipartFile.class);

        given(petRepository.findById(id)).willReturn(Optional.of(pet));

        //when
        petService.updatePet(id, req, file);

        //then
        assertThat(pet.getName()).isEqualTo("삐삐");
        assertThat(pet.getAge()).isEqualTo(2);
        assertThat(pet.getSpecies()).isEqualTo("Cat");
        assertThat(pet.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(pet.getBio()).isEqualTo("시크한 고양이");
        verify(petRepository).updateById(eq(id), eq(pet));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 Pet 수정 시 EntityNotFoundException 발생")
    void updatePet_notFound() {
        //given
        String id = "not_exist_id";
        RegisterRequest req = new RegisterRequest("삐삐", 2, "Cat", Gender.FEMALE, "시크한 고양이");
        MultipartFile file = mock(MultipartFile.class);

        given(petRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> petService.updatePet(id, req, file));

        verify(petRepository).findById(id);

        verify(petRepository, never()).updateById(anyString(), any(Pet.class));
    }

    @Test
    @DisplayName("Pet 삭제 시 존재하는 ID라면 delete가 호출된다.")
    void deletePet_success() {
        //given
        String id = "test_id";
        Pet pet = new Pet(id, "user1", "강아지", 1, "Dog", Gender.MALE, "어릴때 부터 같이 자란 친구입니다!", "s3_key", 0);

        given(petRepository.findById(id)).willReturn(Optional.of(pet));

        //when
        petService.deletePet(id);

        //then
        verify(petRepository).delete(pet);
    }

    @Test
    @DisplayName("Pet 삭제 시 존재하지 않는 ID라면 EntityNotFoundException 발생")
    void deletePet_notFound() {
        //given
        String id = "not_exist_id";
        given(petRepository.findById(id)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> petService.deletePet(id));

        verify(petRepository).findById(id);

        verify(petRepository, never()).delete(any(Pet.class));
    }
}
