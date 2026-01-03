package com.rey.me.service;

import com.rey.me.entity.Job;
import com.rey.me.entity.User;
import com.rey.me.exception.JobNotFoundException;
import com.rey.me.repository.JobRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
public class JobService {

    private JobRepository repo;
    private EmailSenderService emailService;


    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

    public JobService(JobRepository repo, EmailSenderService emailService){
        this.repo=repo;
        this.emailService = emailService;
    }

    public String postJob(Job job) {
        repo.save(job);
        return "JOB POSTED SUCCESSFULLY";

    }

    public List<Page<Job>> getJob(Pageable pageable) {
        return Collections.singletonList(repo.findAll(pageable));
    }

    public List<Page<Job>> getJobByCategory(Pageable pageable, String category) {
        return repo.findByCategory(pageable, category)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));


    }


    public Job getJobById(Long id) {
        return repo.findById(id)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));
    }

    public void deleteJob(Long id){
        repo.deleteById(id);
    }

    public List<Job> searchJob(String search) {
       return repo.search(search)
               .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));
    }

    public Job updateJob(Long id, Job job) {
        Job job1 = repo.findById(id)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));

        job1.setTitle(job.getTitle());
        job1.setCompany(job.getCompany());
        job1.setDescription(job.getDescription());
        job1.setCategory(job.getCategory());
        job1.setLocation(job.getLocation());
        job1.setTechs(job.getTechs());
        job1.setSalary(job.getSalary());

        return repo.save(job1);
    }

    private static final String UPLOAD_DIR = "uploads/";

    public String uploadCV(MultipartFile file) throws IOException, MessagingException {
        //If file is empty or not present
      if(file==null || file.isEmpty()){
          throw new IllegalStateException("File is empty or not present");
      }
            //Check if the directory exist then create new one
      Path uploadPath = Path.of(UPLOAD_DIR);
      if (!Files.exists(uploadPath)){
          Files.createDirectories(uploadPath);
      }

        //Generates a unique filename to avoid overwrites
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName !=null ? originalFileName.substring(originalFileName.lastIndexOf(".")): "";
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        //Saving the file
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());

        User user = new User();
        Job job = new Job();


        user= job.getUser();

        String jobOwnerEmail = user.getEmail();


        emailService.sendFileWithEmail(jobOwnerEmail, originalFileName, filePath.toString());


            return "YOU HAVE APPLIED THIS JOB SUCCESSFULLY";


    }

}
