package com.rey.me.interfaces;

import com.rey.me.dto.JobRequestDto;
import com.rey.me.dto.JobResponseDto;
import com.rey.me.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface JobServiceInterface {
    String postJob(JobRequestDto job, User user);

    Page<JobResponseDto> getJob(Pageable pageable);

    Page<JobResponseDto> getJobByCategory(Pageable pageable, String category);

    JobResponseDto getJobById(Long id);

    String deleteJobById(Long id);

    JobResponseDto updateJob(Long id, JobRequestDto job);

    Page<JobResponseDto> searchJob(String job, Pageable pageable);

    String uploadCV(Long id, MultipartFile file) throws IOException, MessagingException;
}
