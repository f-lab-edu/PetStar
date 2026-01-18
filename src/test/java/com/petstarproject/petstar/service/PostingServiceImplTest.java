package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.PostingRequest;
import com.petstarproject.petstar.entity.Posting;
import com.petstarproject.petstar.enums.Visibility;
import com.petstarproject.petstar.repository.PostingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostingServiceImplTest {

    @Mock
    PostingRepository postingRepository;

    @Mock
    FileStorageService fileStorageService;

    @InjectMocks
    PostingServiceImpl postingService;


    @Test
    @DisplayName("Posting 생성 시 이미지 업로드 수 만큼 FileStorageService.upload가 호출되고 PostingRepository.save가 호출된다")
    void createPosting_success() {
        // given
        String petId = "test_pet_id";
        String ownerId = "test_owner_id";

        PostingRequest req = PostingRequest.builder()
                .petId(petId)
                .title("title")
                .content("content")
                .visibility(Visibility.PUBLIC)
                .build();

        MultipartFile img1 = mock(MultipartFile.class);
        MultipartFile img2 = mock(MultipartFile.class);

        given(img1.isEmpty()).willReturn(false);
        given(img2.isEmpty()).willReturn(false);

        given(fileStorageService.upload(eq(img1), anyString()))
                .willReturn("posts/p1/images/imgKey1");
        given(fileStorageService.upload(eq(img2), anyString()))
                .willReturn("posts/p1/images/imgKey2");

        given(postingRepository.save(any(Posting.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Posting> captor = ArgumentCaptor.forClass(Posting.class);

        // when
        String createdId = postingService.createPosting(req, List.of(img1, img2), ownerId);

        // then
        verify(fileStorageService, times(1)).upload(eq(img1), anyString());
        verify(fileStorageService, times(1)).upload(eq(img2), anyString());

        verify(postingRepository, times(1)).save(captor.capture());
        Posting saved = captor.getValue();

        assertThat(createdId).isEqualTo(saved.getId());
        assertThat(saved.getPetId()).isEqualTo(petId);
        assertThat(saved.getOwnerId()).isEqualTo(ownerId);
        assertThat(saved.getTitle()).isEqualTo("title");
        assertThat(saved.getContent()).isEqualTo("content");
        assertThat(saved.getVisibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(saved.getImageKeys()).containsExactly("posts/p1/images/imgKey1", "posts/p1/images/imgKey2");
    }


    @Test
    @DisplayName("Posting 생성 시 이미지가 비어있으면 upload가 호출되지 않고 save는 호출된다")
    void createPosting_success_noImages() {
        // given
        String petId = "test_pet_id";
        String ownerId = "test_owner_id";

        PostingRequest req = PostingRequest.builder()
                .petId(petId)
                .title("title")
                .content("content")
                .visibility(Visibility.PUBLIC)
                .build();

        given(postingRepository.save(any(Posting.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Posting> captor = ArgumentCaptor.forClass(Posting.class);

        // when
        String createdId = postingService.createPosting(req, null, ownerId);

        // then
        verify(fileStorageService, never()).upload(any(), anyString());
        verify(fileStorageService, never()).delete(anyString());
        verify(postingRepository, times(1)).save(captor.capture());
        Posting saved = captor.getValue();

        assertThat(createdId).isNotNull();
        assertThat(saved.getImageKeys()).isEmpty();
    }


    @Test
    @DisplayName("Posting 생성 중 업로드 실패하면 이미 업로드된 파일은 delete로 롤백된다")
    void createPosting_fail_uploadRollback() {
        // given
        String petId = "test_pet_id";
        String ownerId = "test_owner_id";

        PostingRequest req = PostingRequest.builder()
                .petId(petId)
                .title("title")
                .content("content")
                .visibility(Visibility.PUBLIC)
                .build();

        MultipartFile img1 = mock(MultipartFile.class);
        MultipartFile img2 = mock(MultipartFile.class);

        given(img1.isEmpty()).willReturn(false);
        given(img2.isEmpty()).willReturn(false);

        given(fileStorageService.upload(eq(img1), anyString()))
                .willReturn("posts/p1/images/imgKey1");
        given(fileStorageService.upload(eq(img2), anyString()))
                .willThrow(new RuntimeException("S3 error"));

        // when & then
        assertThatThrownBy(() -> postingService.createPosting(req, List.of(img1, img2), ownerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("S3 error");

        verify(fileStorageService, times(1)).delete(eq("posts/p1/images/imgKey1"));
        verify(fileStorageService, never()).delete("posts/p1/images/imgKey2");
        verify(postingRepository, times(0)).save(any());
    }


    @Test
    @DisplayName("Posting 단건 조회 시 Public이면 requesterId에 상관없이 PostingRepository.findById가 호출되고 응답이 반환된다")
    void getPosting_success_public() {
        // given
        Posting posting = Posting.create(
                "p1", "pet", "owner",
                "title", "content", Visibility.PUBLIC,
                List.of("k1", "k2")
        );

        given(postingRepository.findById("p1")).willReturn(Optional.of(posting));

        // when
        Posting res = postingService.getPosting("p1", "not-owner");

        // then
        verify(postingRepository, times(1)).findById("p1");
        assertThat(res.getId()).isEqualTo("p1");
        assertThat(res.getImageKeys()).containsExactly("k1", "k2");
    }


    @Test
    @DisplayName("PRIVATE Posting은 owner만 조회 가능하다 (requester == owner)")
    void getPosting_success_private_owner() {
        // given
        Posting posting = Posting.create(
                "p1", "pet", "owner",
                "title", "content", Visibility.PRIVATE,
                List.of("k1", "k2")
        );

        given(postingRepository.findById("p1")).willReturn(Optional.of(posting));

        // when
        Posting res = postingService.getPosting("p1", "owner");

        // then
        assertThat(res.getId()).isEqualTo("p1");
        verify(postingRepository, times(1)).findById("p1");
    }


    @Test
    @DisplayName("PRIVATE Posting은 owner가 아니면 AccessDeniedException을 발생시킨다.")
    void getPosting_fail_private_notOwner() {
        // given
        Posting posting = Posting.create(
                "p1", "pet", "owner",
                "title", "content", Visibility.PRIVATE,
                List.of("k1", "k2")
        );

        given(postingRepository.findById("p1")).willReturn(Optional.of(posting));

        // when & then
        Assertions.assertThatThrownBy(() -> postingService.getPosting("p1", "not_owner"))
                .isInstanceOf(RuntimeException.class); // todo: SpringSecurity 추가후 AccessDeniedException으로 수정

        verify(postingRepository, times(1)).findById("p1");
    }


    @Test
    @DisplayName("Posting 단건 조회 시 존재하지 않으면 EntityNotFoundException 발생")
    void getPosting_fail_notFound() {
        // given
        given(postingRepository.findById("p1")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postingService.getPosting("p1", "any_requester"))
                .isInstanceOf(EntityNotFoundException.class);

        verify(postingRepository, times(1)).findById("p1");
    }


    @Test
    @DisplayName("Posting 수정 시 title/content/visibility만 변경된다")
    void updatePosting_success() {
        // given
        Posting posting = Posting.create(
                "posting-1", "pet", "owner",
                "oldTitle", "oldContent", Visibility.PUBLIC,
                List.of("k1", "k2")
        );

        given(postingRepository.findById("posting-1")).willReturn(Optional.of(posting));

        PostingRequest req = PostingRequest.builder()
                .petId("pet-1")
                .title("newTitle")
                .content("newContent")
                .visibility(Visibility.PRIVATE)
                .build();

        // when
        var res = postingService.updatePosting("posting-1", req, "owner");

        // then
        verify(postingRepository, times(1)).findById("posting-1");
        verify(fileStorageService, times(0)).upload(any(), anyString());
        verify(fileStorageService, times(0)).delete(anyString());

        assertThat(res.getTitle()).isEqualTo("newTitle");
        assertThat(res.getContent()).isEqualTo("newContent");
        assertThat(res.getVisibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(res.getImageKeys()).containsExactly("k1", "k2");
    }


    @Test
    @DisplayName("Posting 수정 시 작성자가 아니면 AccessDeniedException 발생")
    void updatePosting_fail_notOwner() {
        // given
        Posting posting = Posting.create(
                "p1", "pet", "owner",
                "oldTitle", "oldContent", Visibility.PUBLIC,
                List.of("k1", "k2")
        );

        given(postingRepository.findById("p1")).willReturn(Optional.of(posting));
        PostingRequest req = mock(PostingRequest.class);

        // when & then
        assertThatThrownBy(() -> postingService.updatePosting("p1", req, "not_owner"))
                .isInstanceOf(RuntimeException.class); // todo: 추후 AccessDeniedException으로 수정 예정

        verify(postingRepository, times(1)).findById("p1");
        verify(fileStorageService, never()).upload(any(), anyString());
        verify(postingRepository, never()).save(any());
    }


    @Test
    @DisplayName("Posting 삭제 시 S3 delete가 이미지 수 만큼 호출되고 PostingRepository.delete가 호출된다")
    void deletePosting_success() {
        // given
        Posting posting = Posting.create(
                "p1", "pet", "owner",
                "title", "content", Visibility.PUBLIC,
                List.of("k1", "k2")
        );

        given(postingRepository.findById("p1")).willReturn(Optional.of(posting));

        // when
        postingService.deletePosting("p1", "owner");

        // then
        verify(fileStorageService, times(1)).deleteAll(eq(List.of("k1", "k2")));
        verify(postingRepository, times(1)).delete(posting);
    }


    @Test
    @DisplayName("Posting 삭제 시 작성자가 아니면 ForbiddenException 발생하고 delete 호출이 없다")
    void deletePosting_forbidden() {
        // given
        Posting posting = Posting.create(
                "p1", "pet", "owner",
                "title", "content", Visibility.PUBLIC,
                List.of("k1")
        );
        given(postingRepository.findById("p1")).willReturn(Optional.of(posting));

        // when & then
        assertThatThrownBy(() -> postingService.deletePosting("p1", "not_owner"))
                .isInstanceOf(RuntimeException.class);

        verify(postingRepository, times(0)).delete(any());
        verify(fileStorageService, times(0)).delete(anyString());
    }
}