package com.REY.Me.Controller;

import com.REY.Me.Entity.Job;
import com.REY.Me.Service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class JobController {

    private JobService service;

    public JobController(JobService service){
        this.service=service;
    }

    @PreAuthorize("hasAnyAuthority['ADMIN','USER']")
    @PostMapping("/job/add")
    public ResponseEntity<String> postJob(@RequestBody Job job){
        return new ResponseEntity<>(service.postJob(job), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority['ADMIN','USER']")
    @GetMapping("/job")
    public ResponseEntity<List<Page<Job>>>getJob(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(service.getJob(pageable), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority['ADMIN','USER']")
    @GetMapping("/job/{category}")
    public ResponseEntity<List<Page<Job>>> getJobByCategory(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size, @PathVariable String category){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(service.getJobByCategory(pageable, category), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority['ADMIN','USER']")
    @GetMapping("/job/{id}")
    public ResponseEntity<Job>JobById(@PathVariable Long id){
        return new ResponseEntity<>(service.getJobById(id), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority['ADMIN']")
    @DeleteMapping("/job/{id}")
    public ResponseEntity<Job>deleteJob(@PathVariable Long id){
        return new ResponseEntity<>(service.getJobById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority['ADMIN','USER']")
    @PostMapping("/job/search")
    public ResponseEntity<List<Job>>searchJob(@RequestParam String search){
        return new ResponseEntity<>(service.searchJob(search), HttpStatus.OK);
    }





}
