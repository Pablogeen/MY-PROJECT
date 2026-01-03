package com.rey.me.controller;

import com.rey.me.entity.Job;
import com.rey.me.service.JobService;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/job")
public class JobController {

    private JobService service;

    public JobController(JobService service){
        this.service=service;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/add")
    public ResponseEntity<String> postJob(@RequestBody Job job){
        return new ResponseEntity<>(service.postJob(job), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping()
    public ResponseEntity<List<Page<Job>>>getJob(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(service.getJob(pageable), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{category}")
    public ResponseEntity<List<Page<Job>>> getJobByCategory(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size, @PathVariable String category){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(service.getJobByCategory(pageable, category), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Job>JobById(@PathVariable Long id){
        return new ResponseEntity<>(service.getJobById(id), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('ADMIN') or @JobSecurity.isJobOwner(authentication, #id)")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Job>deleteJob(@PathVariable Long id){
        return new ResponseEntity<>(service.getJobById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @JobSecurity.isJobOwner(authentication, #id)")
    @PutMapping("update/{id}")
    public ResponseEntity<Job>updateJob(@PathVariable Long id, @RequestBody Job job){
        return new ResponseEntity<>(service.updateJob(id, job), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/search")
    public ResponseEntity<List<Job>>searchJob(@RequestParam String search){
        return new ResponseEntity<>(service.searchJob(search), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('USER'")
    @PostMapping("/apply")
    public ResponseEntity<String> uploadCV(@RequestPart MultipartFile file) throws IOException, MessagingException {
        return new ResponseEntity<>(service.uploadCV(file), HttpStatus.OK);
    }


}
