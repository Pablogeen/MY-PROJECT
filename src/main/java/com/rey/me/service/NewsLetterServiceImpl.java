package com.rey.me.service;

import com.rey.me.dto.NewsLetterRequestDto;
import com.rey.me.dto.NewsLetterResponseDto;
import com.rey.me.entity.NewsLetter;
import com.rey.me.entity.User;
import com.rey.me.interfaces.NewsLetterServiceInterface;
import com.rey.me.repository.NewsLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsLetterServiceImpl implements NewsLetterServiceInterface {

  private final NewsLetterRepository letterRepo;
  private final ModelMapper modelMapper;

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = "newsletters", key = "#result.id"),
            evict = @CacheEvict(value = "allNewsletters", allEntries = true)
    )
    public NewsLetterResponseDto createNewsLetter(NewsLetterRequestDto letterDTO, User user) {
        NewsLetter mappedNews = modelMapper.map(letterDTO, NewsLetter.class);
        log.info("Mapped news letter request into news letter");
        mappedNews.setUser(user);
        NewsLetter savedNews = letterRepo.save(mappedNews);
        log.info("News created successfully");

        NewsLetterResponseDto response = modelMapper.map(savedNews, NewsLetterResponseDto.class);
        log.info("Mapped saved newsletter to response DTO");
        return response;
    }

    @Override
    @Cacheable(value = "allNewsletters", key = "#pageRequest.pageNumber + '-' + #pageRequest.pageSize + '-' + #pageRequest.sort.toString()")
    public Page<NewsLetterResponseDto> getNewsLetter(Pageable pageRequest) {
        log.info("Request to get news Letter");
        Page<NewsLetter> newsLetter = letterRepo.findAll(pageRequest);
        log.info("Gotten newsLetter from the Database");
        Page<NewsLetterResponseDto> mappedLetter =
                newsLetter.map(news -> modelMapper.map(news, NewsLetterResponseDto.class));

        log.info("Mapped newsLetter into Response Dto");
        return mappedLetter;
    }

    @Override
    @Cacheable(value = "newsletters", key = "#id")
    public NewsLetterResponseDto readNewsById(Long id) {
        NewsLetter newsLetter = letterRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("News Letter Not Found"));
        log.info("News letter gotten from Database: {}", newsLetter);
        NewsLetterResponseDto response = modelMapper.map(newsLetter, NewsLetterResponseDto.class);
        log.info("News Letter mapped into News Letter Response");
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "newsletters", key = "#id"),
            @CacheEvict(value = "allNewsletters", allEntries = true)
    })
    public void deleteNewsById(Long id) {
        NewsLetter newsLetter = letterRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("News Letter Not Found"));
        log.info("News Letter found");
        letterRepo.delete(newsLetter);
        log.info("News Letter deleted Successfully");
    }

}
