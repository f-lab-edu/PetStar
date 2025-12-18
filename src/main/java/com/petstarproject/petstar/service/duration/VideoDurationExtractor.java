package com.petstarproject.petstar.service.duration;

import org.springframework.web.multipart.MultipartFile;

/**
 * video duration을 추출하기위한 interface
 */
public interface VideoDurationExtractor {
    /**
     *
     * @param videoSource 영상 파일
     * @return 영상길이(초)
     */
    int extractDurationSec(MultipartFile videoSource);
}
