package com.REY.Me.Repository;

import com.REY.Me.Entity.NewsLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsLetterRepository extends JpaRepository<NewsLetter, Long> {


    @Query(value = "SELECT user_id FROM NewsLetter WHERE id =:id", nativeQuery = true)
    Long findNewsPostedById(Long id);

    @Query(value = "SELECT * FROM NewsLetter ORDER BY timePosted ASC", nativeQuery = true)
    List<NewsLetter> getNewsByOrderOfDate();
}
