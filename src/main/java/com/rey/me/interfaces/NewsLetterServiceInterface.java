package com.rey.me.interfaces;

import com.rey.me.dto.NewsLetterRequestDto;
import com.rey.me.dto.NewsLetterResponseDto;
import com.rey.me.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface NewsLetterServiceInterface {

    NewsLetterResponseDto createNewsLetter(NewsLetterRequestDto requestDto, User user);

    Page<NewsLetterResponseDto> getNewsLetter(Pageable pageRequest);

    NewsLetterResponseDto readNewsById(Long id);

    void deleteNewsById(Long id);
}
