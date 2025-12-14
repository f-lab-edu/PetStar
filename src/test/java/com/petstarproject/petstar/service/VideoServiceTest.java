package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.VideoCreateRequest;
import com.petstarproject.petstar.dto.VideoUpdateRequest;
import com.petstarproject.petstar.entity.Video;
import com.petstarproject.petstar.enums.Visibility;
import com.petstarproject.petstar.exception.VideoSourceRequiredException;
import com.petstarproject.petstar.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private VideoServiceImpl videoService;


    @Test
    @DisplayName("Video 생성 시 source, thumbnail 업로드 수 VideoRepository save가 호출된다")
    void createVideo_success() {
        // given
        String petId = "test_pet_id";
        VideoCreateRequest req = VideoCreateRequest.builder()
                .title("title")
                .description("description")
                .tags(List.of("tag1,", "tag2"))
                .build();

        MultipartFile videoSource = mock(MultipartFile.class);
        MultipartFile thumbnail = mock(MultipartFile.class);

        given(videoSource.isEmpty()).willReturn(false);
        given(thumbnail.isEmpty()).willReturn(false);

        given(fileStorageService.upload(eq(videoSource), anyString()))
                .willReturn("videos/test_pet_id/source/sourceKey");
        given(fileStorageService.upload(eq(thumbnail), anyString()))
                .willReturn("videos/test_pet_id/thumbnail/thumbnailKey");

        ArgumentCaptor<Video> captor = ArgumentCaptor.forClass(Video.class);

        // when
        String videoId = videoService.createVideo(req, videoSource, thumbnail, petId);

        // then
        verify(fileStorageService, times(1)).upload(eq(videoSource), anyString());
        verify(fileStorageService, times(1)).upload(eq(thumbnail), anyString());

        verify(videoRepository, times(1)).save(captor.capture());
        Video saved = captor.getValue();

        assertThat(videoId).isNotNull();
        assertThat(saved.getPetId()).isEqualTo(petId);
        assertThat(saved.getSourceKey()).isEqualTo("videos/test_pet_id/source/sourceKey");
        assertThat(saved.getThumbnailKey()).isEqualTo("videos/test_pet_id/thumbnail/thumbnailKey");
    }


    @Test
    @DisplayName("source가 없으면 Video 생성 시 VideoSourceRequiredException이 발생하고 저장/업로드가 호출되지 않는다.")
    void createVideo_fail_sourceRequired() {
        // given
        VideoCreateRequest req = VideoCreateRequest.builder()
                .title("title")
                .description("description")
                .tags(List.of("tag1,", "tag2"))
                .build();

        MultipartFile emptySource = mock(MultipartFile.class);
        given(emptySource.isEmpty()).willReturn(true);

        // when & then
        assertThatThrownBy(() -> videoService.createVideo(req, emptySource, null, "test_pet_id"))
                .isInstanceOf(VideoSourceRequiredException.class);

        verify(fileStorageService, never()).upload(any(), anyString());
        verify(videoRepository, never()).save(any());
    }


    @Test
    @DisplayName("존재하는 ID로 Video를 조회하면 Video를 반환한다")
    void getVideo_success() {
        // given
        String id = "test_video_id";
        Video video = Video.create(
                id,
                "test_pet_id",
                "title",
                "description",
                "sourceKey",
                "thumbnailKey",
                0,
                List.of("tag")
        );

        given(videoRepository.findById(id)).willReturn(Optional.of(video));

        // when
        Video res = videoService.getVideo(id);

        // then
        assertThat(res.getId()).isEqualTo(id);
        verify(videoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("존재하는 ID로 Video를 조회하면 Video를 반환한다")
    void getVideo_fail_notFound() {
        // given
        String id = "test_video_id";
        given(videoRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> videoService.getVideo(id))
                .isInstanceOf(EntityNotFoundException.class);
        verify(videoRepository, times(1)).findById(id);
    }


    @Test
    @DisplayName("Video 수정 시 메타데이터가 업데이트 된다 (thumbnail 없음)")
    void updateVideo_success_metadataOnly() {

        // given
        String videoId = "test_video_id";

        Video video = Video.create(
                videoId,
                "test_pet_id",
                "oldTitle",
                "oldDescription",
                "sourceKey",
                "oldThumbnailKey",
                0,
                List.of("oldTag")
        );

        given(videoRepository.findById(videoId)).willReturn(Optional.of(video));

        VideoUpdateRequest req = VideoUpdateRequest.builder()
                .title("newTitle")
                .description("newDescription")
                .visibility(Visibility.PRIVATE)
                .tags(List.of("tag1", "tag2"))
                .build();

        // when
        videoService.updateVideo(videoId, req, null);

        // then
        verify(videoRepository, times(1)).findById(videoId);
        verify(fileStorageService, never()).upload(any(), anyString());

        assertThat(video.getTitle()).isEqualTo("newTitle");
        assertThat(video.getDescription()).isEqualTo("newDescription");
        assertThat(video.getVisibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(video.getTags()).containsExactly("tag1", "tag2");
        assertThat(video.getThumbnailKey()).isEqualTo("oldThumbnailKey");

    }


    @Test
    @DisplayName("Video 수정 시 thumbnailKey와 메타데이터가 업데이트 된다.")
    void updateVideo_success_withThumbnail() {
        // given
        String videoId = "test_video_id";

        Video video = Video.create(
                videoId,
                "test_pet_id",
                "oldTitle",
                "oldDescription",
                "sourceKey",
                "oldThumbnailKey",
                0,
                List.of("oldTag")
        );

        given(videoRepository.findById(videoId)).willReturn(Optional.of(video));

        MultipartFile thumbnail = mock(MultipartFile.class);
        given(thumbnail.isEmpty()).willReturn(false);

        given(fileStorageService.upload(eq(thumbnail), anyString()))
                .willReturn("videos/test_video_id/thumbnail/newThumbKey");

        VideoUpdateRequest req = VideoUpdateRequest.builder()
                .title("title") // 변경 없어도 됨
                .build();


        // when
        videoService.updateVideo(videoId, req, thumbnail);

        // then
        verify(videoRepository, times(1)).findById(videoId);
        verify(fileStorageService, times(1)).upload(eq(thumbnail), anyString());

        assertThat(video.getThumbnailKey()).isEqualTo("videos/test_video_id/thumbnail/newThumbKey");
    }


    @Test
    @DisplayName("Video 수정 시 대상이 없으면 EntityNotFoundException이 발생한다")
    void updateVideo_fail_notFound() {
        // given
        String videoId = "not_exist_id";
        given(videoRepository.findById(videoId)).willReturn(Optional.empty());

        VideoUpdateRequest req = VideoUpdateRequest.builder()
                .title("newTitle")
                .build();

        // when & then
        assertThatThrownBy(() -> videoService.updateVideo(videoId, req, null))
                .isInstanceOf(EntityNotFoundException.class);

        verify(videoRepository, times(1)).findById(videoId);
        verify(fileStorageService, never()).upload(any(), anyString());
    }


    @Test
    @DisplayName("Video 삭제 시 Repository delete가 호출된다.")
    void deleteVideo_success() {
        // given
        String videoId = "test_video_id";

        Video video = Video.create(
                videoId,
                "test_pet_id",
                "title",
                "description",
                "sourceKey",
                "thumbKey",
                0,
                List.of()
        );

        given(videoRepository.findById(videoId)).willReturn(Optional.of(video));

        // when
        videoService.deleteVideo(videoId);

        //then
        verify(videoRepository, times(1)).findById(videoId);
        verify(videoRepository, times(1)).delete(video);
    }


    @Test
    @DisplayName("Video 삭제 시 대상이 없으면 EntityNotFoundException에러가 발생하고 delete는 호출되지 않는다.")
    void deleteVideo_fail_notFound() {
        // given
        String videoId = "test_video_id";

        given(videoRepository.findById(videoId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> videoService.deleteVideo(videoId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(videoRepository, times(1)).findById(videoId);
        verify(videoRepository, never()).delete(any());
    }
}
