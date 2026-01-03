package com.rey.me.repository;

import com.rey.me.entity.NewsLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsLetterRepository extends JpaRepository<NewsLetter, Long> {


    @Query(value = "SELECT user_id FROM news_letter WHERE id =:id", nativeQuery = true)
    Long findNewsPostedById(Long id);

    @Query(value = "SELECT * FROM news_letter ORDER BY time_posted ASC", nativeQuery = true)
    List<NewsLetter> getNewsByOrderOfDate();
}
