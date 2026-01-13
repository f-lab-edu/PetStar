package com.petstarproject.petstar.controller;

import com.petstarproject.petstar.dto.MessageResponse;
import com.petstarproject.petstar.dto.PostingRequest;
import com.petstarproject.petstar.dto.PostingResponse;
import com.petstarproject.petstar.service.PostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/posts")
public class PostingController {

    private final PostingService postingService;

    @Autowired
    PostingController(PostingService postingService) {
        this.postingService = postingService;
    }

    @GetMapping("/{postingId}")
    public ResponseEntity<PostingResponse> getPosting(@PathVariable String postingId,
                                        @RequestHeader(value = "X-REQUESTER-ID", required = false) String requesterId) {
        return ResponseEntity.ok(PostingResponse.from(postingService.getPosting(postingId, requesterId)));
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createPosting(@RequestPart("request") PostingRequest request,
                                           @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                           @RequestHeader(value = "X-REQUESTER-ID") String requesterId
    ) {
        String postingId = postingService.createPosting(request, images, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(postingId));
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체


    @PatchMapping(value = "/{postingId}")
    public ResponseEntity<Void> updatePosting(
            @PathVariable String postingId,
            @RequestBody PostingRequest request,
            @RequestHeader(value = "X-REQUESTER-ID") String requesterId
    ) {
        postingService.updatePosting(postingId, request, requesterId);
        return ResponseEntity.noContent().build();
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체


    @DeleteMapping("/{postingId}")
    public ResponseEntity<Void> deletePosting(@PathVariable String postingId,
                                           @RequestHeader(value = "X-REQUESTER-ID") String requesterId) {
        postingService.deletePosting(postingId, requesterId);
        return ResponseEntity.noContent().build();
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체
}
