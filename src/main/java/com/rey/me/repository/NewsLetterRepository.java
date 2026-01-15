package com.rey.me.repository;

import com.rey.me.entity.NewsLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsLetterRepository extends JpaRepository<NewsLetter, Long> {

    @Query(value = "SELECT n.user.id FROM NewsLetter n WHERE n.id =:id")
    Long findUserByNewsLetter(Long id);
}
