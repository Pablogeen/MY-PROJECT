package com.rey.me.repository;

import com.rey.me.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByCategory(Pageable pageable, String category);

    @Query(value = "SELECT * FROM job WHERE lower(title) LIKE lower(concat('%', :search, '%'))" +
            "OR lower(description) LIKE lower(concat('%', :search, '%'))" +
            "OR lower(company) LIKE lower(concat('%', :word, '%'))" +
            "OR lower(location) LIKE lower(concat('%', :search, '%'))" +
            "OR lower(techs) LIKE lower(concat('%', :search, '%'))"+
            "OR lower(salary) LIKE lower(concat('%', :search, '%'))" +
            "OR lower(category) LIKE lower(concat('%', :search, '%'))" , nativeQuery = true)
    Optional<Page<Job>> search(String search, Pageable pageable);

    @Query(value = "SELECT user_id FROM Job WHERE id =:id", nativeQuery = true)
    Long findUserByJobId(Long id);
}
