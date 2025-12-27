package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.VideoInfoRequest;
import com.petstarproject.petstar.entity.Video;
import com.petstarproject.petstar.enums.VideoFileType;
import com.petstarproject.petstar.enums.Visibility;
import com.petstarproject.petstar.exception.VideoSourceRequiredException;
import com.petstarproject.petstar.repository.VideoRepository;
import com.petstarproject.petstar.service.duration.VideoDurationExtractor;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {
    /**
     * todo:
     *  1. VideoRepository 주입
     *  2. 업로드 시 Video 엔티티 생성 후 저장
     *  3. 수정 시 제목/설명/썸네일 등 변경 로직 구현
     *  4. 삭제 시 소유자(ownerId) 검증 후 삭제 처리
     *  5. 단건 조회 시 존재하지 않는 경우 예외 처리 (예: `EntityNotFoundException`)
     *  6. 파일 업로드/저장 전략과 연동 (이미 Pet 모듈에서 사용하는 FileStorageService 재사용 가능 여부 검토)
     */

    private final VideoRepository videoRepository;
    private final FileStorageService fileStorageService;
    private final VideoDurationExtractor videoDurationExtractor; // todo: mp4 제한 없이 FFmpeg로 확장

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository, FileStorageService fileStorageService, VideoDurationExtractor videoDurationExtractor) {
        this.videoRepository = videoRepository;
        this.fileStorageService = fileStorageService;
        this.videoDurationExtractor = videoDurationExtractor;
    }

    @Transactional
    @Override
    public String createVideo(VideoInfoRequest request, MultipartFile videoSource, MultipartFile thumbnail, String petId, String userId) {
        if (videoSource == null || videoSource.isEmpty()) {
            throw new VideoSourceRequiredException("Video source file is required");
        }

        String videoId = UUID.randomUUID().toString();

        int durationSec = videoDurationExtractor.extractDurationSec(videoSource);

        String sourceKey = uploadFileIfPresent(videoSource, VideoFileType.VIDEO, videoId);
        String thumbnailKey = uploadFileIfPresent(thumbnail, VideoFileType.THUMBNAIL, videoId);

        Video video = Video.create(
                videoId,
                petId,
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getVisibility(),
                sourceKey,
                thumbnailKey,
                durationSec,
                request.getTags()
        );

        videoRepository.save(video);

        return videoId;
    }

    @Override
    public Video getVideo(String videoId, String requesterId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("video not found: " + videoId));

        if (video.getVisibility() == Visibility.PRIVATE) {
            checkOwner(video.getOwnerId(), requesterId);
        }
        return video;
    }

    /**
     * JPA의 dirty-checking 특성을 활용해서 record를 update
     * @param id 동영상의 ID
     * @param request 클라이언트로 부터 전달받은 수정할 메타데이터
     * @param thumbnail 클라이언트로 부터 전달받은 새 썸네일
     */
    @Transactional
    @Override
    public void updateVideo(String id, VideoInfoRequest request, MultipartFile thumbnail, String requesterId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("video not found: " + id));

        checkOwner(video.getOwnerId(), requesterId);
        video.updateMeta(request.getTitle(), request.getDescription(), request.getVisibility(), request.getTags());

        String thumbnailKey = uploadFileIfPresent(thumbnail, VideoFileType.THUMBNAIL, id);
        if (thumbnailKey != null) video.updateThumbnail(thumbnailKey);

    }

    @Transactional
    @Override
    public void deleteVideo(String videoId, String requesterId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("video not found: " + videoId));
        checkOwner(video.getOwnerId(), requesterId);
        // Todo: S3 삭제
        videoRepository.delete(video);
    }

    private String uploadFileIfPresent(MultipartFile file, VideoFileType type, String videoId) {
        if (file == null || file.isEmpty()) return null;

        String fileKey = switch (type) {
            case VIDEO -> String.format("videos/%s/source/%s", videoId, UUID.randomUUID());
            case THUMBNAIL -> String.format("videos/%s/thumbnail/%s", videoId, UUID.randomUUID());
        };

        return fileStorageService.upload(file, fileKey);
    }

    private void checkOwner(String ownerId, String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            throw new RuntimeException("인증이 필요합니다."); // SpringSecurity 추가 전 임시 코드
//            throw new AccessDeniedException("authentication required");
        }
        if (!requesterId.equals(ownerId)) {
            throw new RuntimeException("본인이 만든 동영상이 아닙니다."); // SpringSecurity 추가 전 임시 코드
//            throw new AccessDeniedException("not owner");
        }
    }
}
