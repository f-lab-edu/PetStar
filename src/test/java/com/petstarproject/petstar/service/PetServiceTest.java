package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.RegisterRequest;
import com.petstarproject.petstar.entity.Pet;
import com.petstarproject.petstar.enums.Gender;
import com.petstarproject.petstar.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private FileStorageService fileStorageService;

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
    @DisplayName("이미지와 함께 Pet 등록 시 S3 업로드 후 Repository save가 호출된다")
    void registerPet_success_withImage() {
        // given
        RegisterRequest req = new RegisterRequest("삐삐", 1, "Cat", Gender.FEMALE, "시크한 고양이");
        String userId = "test_id";

        MultipartFile file = mock(MultipartFile.class);

        given(file.isEmpty()).willReturn(false);

        given(fileStorageService.upload(any(), anyString()))
                .willReturn("pets/someId/profile/someKey");

        ArgumentCaptor<Pet> petArgumentCaptor = ArgumentCaptor.forClass(Pet.class);

        // when
        petService.registerPet(req, file, userId);

        // then
        verify(fileStorageService, times(1)).upload(any(), anyString());

        verify(petRepository, times(1)).save(petArgumentCaptor.capture());
        Pet saved = petArgumentCaptor.getValue();

        assertThat(saved.getProfileImageKey()).isEqualTo("pets/someId/profile/someKey");
    }

    @Test
    @DisplayName("이미지 없이 Pet 등록 시 Repository save가 호출되고 profileImageKey는 null로 저장된다")
    void registerPet_success_withoutImage() {
        //given
        RegisterRequest req = new RegisterRequest("삐삐", 1, "Cat", Gender.FEMALE, "시크한 고양이");
        String id = "test_id";
        MultipartFile file = mock(MultipartFile.class);

        given(file.isEmpty()).willReturn(true);

        ArgumentCaptor<Pet> petArgumentCaptor = ArgumentCaptor.forClass(Pet.class);

        //when
        petService.registerPet(req, file, id);

        //then
        verify(petRepository).save(petArgumentCaptor.capture());
        Pet saved = petArgumentCaptor.getValue();

        assertThat(saved.getProfileImageKey()).isNull();
        assertThat(saved.getName()).isEqualTo("삐삐");

        verify(fileStorageService, never()).upload(any(), anyString());
    }

    @Test
    @DisplayName("존재하는 ID로 Pet 수정 시 이미지가 있으면 S3에 업로드 되고 정보가 업데이트된다")
    void updatePet_success_withImage() {
        //given
        String id = "test_id";
        Pet pet = new Pet(id, "user1", "강아지", 1, "Dog", Gender.MALE, "어릴때 부터 같이 자란 친구입니다!", "s3_key", 0);
        RegisterRequest req = new RegisterRequest("삐삐", 2, "Cat", Gender.FEMALE, "시크한 고양이");
        MultipartFile file = mock(MultipartFile.class);

        given(file.isEmpty()).willReturn(false);

        given(petRepository.findById(id)).willReturn(Optional.of(pet));
        given(fileStorageService.upload(any(MultipartFile.class), anyString())).willReturn("new_s3_key");

        //when
        petService.updatePet(id, req, file);

        //then
        assertThat(pet.getName()).isEqualTo("삐삐");
        assertThat(pet.getAge()).isEqualTo(2);
        assertThat(pet.getSpecies()).isEqualTo("Cat");
        assertThat(pet.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(pet.getBio()).isEqualTo("시크한 고양이");
        assertThat(pet.getProfileImageKey()).isEqualTo("new_s3_key");

        verify(petRepository, times(1)).findById(id);
        verify(fileStorageService, times(1)).upload(any(MultipartFile.class), anyString());
        verify(petRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 ID로 Pet 수정 시 이미지가 없으면 S3 업로드 없이 정보만 업데이트된다")
    void updatePet_success_withoutImage() {
        // given
        String id = "test_id";
        Pet pet = new Pet(id, "user1", "강아지", 1, "Dog", Gender.MALE, "어릴때 부터 같이 자란 친구입니다!", "old_s3_key", 0);
        RegisterRequest req = new RegisterRequest("삐삐", 2, "Cat", Gender.FEMALE, "시크한 고양이");
        MultipartFile file = mock(MultipartFile.class);

        given(file.isEmpty()).willReturn(true);

        given(petRepository.findById(id)).willReturn(Optional.of(pet));

        // when
        petService.updatePet(id, req, file);

        // then
        assertThat(pet.getName()).isEqualTo("삐삐");
        assertThat(pet.getAge()).isEqualTo(2);
        assertThat(pet.getSpecies()).isEqualTo("Cat");
        assertThat(pet.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(pet.getBio()).isEqualTo("시크한 고양이");

        assertThat(pet.getProfileImageKey()).isEqualTo("old_s3_key");

        verify(petRepository, times(1)).findById(id);
        verify(fileStorageService, never()).upload(any(), anyString());
        verify(petRepository, never()).save(any());
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

        verify(petRepository, times(1)).findById(id);
        verify(petRepository, never()).save(any());
        verify(fileStorageService, never()).upload(any(), anyString());
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
