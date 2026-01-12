package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.PostingRequest;
import com.petstarproject.petstar.entity.Posting;
import com.petstarproject.petstar.enums.Visibility;
import com.petstarproject.petstar.repository.PostingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PostingServiceImpl implements PostingService {

    private final PostingRepository postingRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public PostingServiceImpl(PostingRepository postingRepository, FileStorageService fileStorageService) {
        this.postingRepository = postingRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public String createPosting(PostingRequest request,
                                 List<MultipartFile> images,
                                 String requesterId) {
        String postingId = UUID.randomUUID().toString();

        List<String> uploadedKeys = new ArrayList<>();
        try {
            // 이미지 업로드
            if (images != null) {
                for (MultipartFile image : images) {
                    if (image == null || image.isEmpty()) continue;

                    String key = String.format("posts/%s/images/%s", postingId, UUID.randomUUID());
                    uploadedKeys.add(fileStorageService.upload(image, key));
                }
            }

            // 엔티티 생성
            Posting posting = Posting.create(
                    postingId,
                    request.getPetId(),
                    requesterId,
                    request.getTitle(),
                    request.getContent(),
                    request.getVisibility(),
                    uploadedKeys
            );

            // DB 저장
            Posting saved = postingRepository.save(posting);

            return saved.getId();

        } catch (RuntimeException e) {
            // 업로드된 파일이 있다면 보상 삭제(롤백)
            for (String key : uploadedKeys) {
                try { fileStorageService.delete(key); }
                catch (Exception deleteEx) {
                    log.warn("임시저장 이미지 삭제 실패: postingId={}, key={}", postingId, key, deleteEx);
                }
            }
            throw e;
        }
    }

    @Override
    public Posting getPosting(String postingId, String requesterId) {
        // 엔티티 조회
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new EntityNotFoundException("Posting not found: " + postingId));

        // 권한 검증
        if (posting.getVisibility() == Visibility.PRIVATE) {
            checkOwner(posting.getOwnerId(), requesterId);
        }

        return posting;
    }

    @Override
    @Transactional
    public Posting updatePosting(String postingId, PostingRequest request, String requesterId) {
        // 엔티티 조회
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new EntityNotFoundException("Posting not found: " + postingId));

        //권한 검증
        checkOwner(posting.getOwnerId(), requesterId);

        // 정보 업데이트
        posting.updateMeta(request.getTitle(), request.getContent(), request.getVisibility());

        return posting;
    }

    @Override
    public void deletePosting(String postingId, String requesterId) {
        // 엔티티 조회
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new EntityNotFoundException("Posting not found: " + postingId));

        // 권한 검증
        checkOwner(posting.getOwnerId(), requesterId);

        // s3 이미지 삭제
        fileStorageService.deleteAll(posting.getImageKeys());

        // 엔티티 삭제
        postingRepository.delete(posting);
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
