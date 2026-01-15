package com.rey.me.controller;

import com.rey.me.dto.NewsLetterRequestDto;
import com.rey.me.dto.NewsLetterResponseDto;
import com.rey.me.entity.User;
import com.rey.me.interfaces.NewsLetterServiceInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/news-letter")
@RequiredArgsConstructor
@Slf4j
public class NewsLetterController {

    private final NewsLetterServiceInterface serviceInterface;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/post")
    public ResponseEntity<NewsLetterResponseDto>createLetter(@Valid
            @RequestBody NewsLetterRequestDto requestDto, @AuthenticationPrincipal User user) {
        log.info("Request to post a news letter");
        NewsLetterResponseDto response = serviceInterface.createNewsLetter(requestDto, user);
        log.info("News Letter created Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping()
    public ResponseEntity<Page<NewsLetterResponseDto>>readNewsLetter(
            @RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "10") @Max(10) int size){
        log.info("Request made to retrieve all newsLetter");
        Pageable pageRequest = PageRequest.of(page,size);
        Page<NewsLetterResponseDto> newsResponse = serviceInterface.getNewsLetter(pageRequest);
        log.info("All news letter found successfully");
            return new ResponseEntity<>(newsResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<NewsLetterResponseDto>readNewsById(@PathVariable Long id){
        log.info("Getting news by id: {}",id);
        NewsLetterResponseDto newsResponse = serviceInterface.readNewsById(id);
        return new ResponseEntity<>(newsResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN') or @NewsLetterSecurity.isNewsOwner(authentication, #id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id){
        log.info("Request made to delete News Letter with id: {}", id);
        serviceInterface.deleteNewsById(id);
        log.info("News Deleted Successfully");
        return ResponseEntity.noContent().build();
    }



}
