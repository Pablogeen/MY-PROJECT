package com.rey.me.controller;

import com.rey.me.dto.JobRequestDto;
import com.rey.me.dto.JobResponseDto;
import com.rey.me.entity.User;
import com.rey.me.interfaces.JobServiceInterface;
import jakarta.mail.MessagingException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobServiceInterface serviceInterface;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/add")
    public ResponseEntity<String> postJob(@RequestBody JobRequestDto job, @AuthenticationPrincipal User user){
        log.info("Request has been made to post a job: {}",job);
        String jobResponse = serviceInterface.postJob(job, user);
        return new ResponseEntity<>(jobResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping()
    public ResponseEntity<Page<JobResponseDto>>getJob(
            @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> jobResponse = serviceInterface.getJob(pageable);
        return new ResponseEntity<>(jobResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<JobResponseDto>> getJobByCategory(
                     @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size,
                                    @PathVariable String category){
        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> jobResponse = serviceInterface.getJobByCategory(pageable, category);
        return new ResponseEntity<>(jobResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/id/{id}")
    public ResponseEntity<JobResponseDto>getJobById(@PathVariable Long id){
        log.info("Getting details of job with id: {}", id);
        JobResponseDto jobResponse = serviceInterface.getJobById(id);
        log.info("Job found: {}",jobResponse);
        return new ResponseEntity<>(jobResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @JobSecurity.isJobOwner(authentication, #id)")
    @DeleteMapping("{id}/delete")
    public ResponseEntity<String>deleteJob(@PathVariable Long id){
        log.info("About to delete job with id: {}",id);
        String deletedResponse = serviceInterface.deleteJobById(id);
        log.info("Job Deleted successfully");
        return new ResponseEntity<>(deletedResponse, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @JobSecurity.isJobOwner(authentication, #id)")
    @PutMapping("{id}/update")
    public ResponseEntity<JobResponseDto>updateJob(@PathVariable Long id, @RequestBody JobRequestDto job){
        log.info("Request to update job with id: {}", id);
        JobResponseDto jobResponse = serviceInterface.updateJob(id, job);
        log.info("Job updated successfully: {}",jobResponse);
        return new ResponseEntity<>(jobResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/search")
    public ResponseEntity<Page<JobResponseDto>>searchJob(
            @RequestParam String job, @RequestParam("page")int page, @RequestParam("size")int size){
        log.info("Request made to search for a job: {}",job);
        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> jobResponse = serviceInterface.searchJob(job, pageable);
        log.info("Jobs found related to search: {}",jobResponse);
        return new ResponseEntity<>(jobResponse, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{id}/apply")
    public ResponseEntity<String> uploadCV(
            @PathVariable Long id, @RequestPart MultipartFile file) throws IOException, MessagingException {
        log.info("Request to apply for job with id: {}",id);
        String applicationResponse = serviceInterface.uploadCV(id, file);
        log.info("Applied to job successfully");
        return new ResponseEntity<>(applicationResponse, HttpStatus.OK);
    }


}
