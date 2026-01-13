package com.rey.me.service;

import com.rey.me.dto.JobRequestDto;
import com.rey.me.dto.JobResponseDto;
import com.rey.me.entity.Job;
import com.rey.me.entity.User;
import com.rey.me.exception.JobNotFoundException;
import com.rey.me.interfaces.JobServiceInterface;
import com.rey.me.repository.JobRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobServiceInterface {

    private final JobRepository repo;
    private final EmailSenderService emailService;
    private final ModelMapper modelMapper;

    public String postJob(JobRequestDto jobRequest, User user) {
        Job job = modelMapper.map(jobRequest, Job.class);
        log.info("Job request mapped into job entity: {}",job);
        job.setUser(user);
        repo.save(job);
        log.info("Job save successfully");
        return "JOB POSTED SUCCESSFULLY";
    }

    public Page<JobResponseDto> getJob(Pageable pageable) {
        log.info("Getting job");
        Page<Job> jobResponse = repo.findAll(pageable);
        log.info("Gotten all jobs from the DB");
        Page<JobResponseDto> mappedResponse =
                jobResponse.map(job -> modelMapper.map(job, JobResponseDto.class));
        log.info("Mapped job into JobResponseDto: {}",mappedResponse);
        return mappedResponse;
    }

    public Page<JobResponseDto> getJobByCategory(Pageable pageable, String category) {
        Page<Job> jobCategory = repo.findByCategory(pageable, category);
        log.info("Jobs found from the category: {}",category);
        Page<JobResponseDto> mappedResponse =
                jobCategory.map(job -> modelMapper.map(job, JobResponseDto.class));
        log.info("Mapped job into jobResponse: {}",mappedResponse);
        return mappedResponse;
    }


    public JobResponseDto getJobById(Long id) {
        Job job = repo.findById(id)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));
        log.info("Search the DB for job with id: {}",id);
        JobResponseDto jobResponse = modelMapper.map(job, JobResponseDto.class);
        log.info("Mapped job to jobResponse: {}",jobResponse);
        return jobResponse;
    }

    public String deleteJobById(Long id){
        Job job = repo.findById(id)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));
        log.info("Checking if job exists");
        repo.delete(job);
        log.info("Job deleted");
        return "JOB DELETED SUCCESSFULLY";
    }

    public Page<JobResponseDto> searchJob(String job, Pageable pageable) {
        if (job == null) {
            throw new IllegalStateException("Invalid Request: Job not found");
        }
        Page<Job> searchedJob = repo.search(job, pageable)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));
        log.info("Gotten searchedJob from DB");
      Page<JobResponseDto> mappedResponse =
              searchedJob.map(jobb -> modelMapper.map(jobb, JobResponseDto.class));
      log.info("Mapped job into JobResponse: {}",mappedResponse);
      return mappedResponse;
    }

    public JobResponseDto updateJob(Long id, JobRequestDto jobRequest) {
         repo.findById(id)
                .orElseThrow(()-> new JobNotFoundException("JOB NOT FOUND"));
        log.info("Checked if job exists");

        Job updatedJob = modelMapper.map(jobRequest, Job.class);
        log.info("Mapped jobRequest into job: {}",updatedJob);

        repo.save(updatedJob);
        log.info("Saved job successfully");

        JobResponseDto jobResponse = modelMapper.map(updatedJob, JobResponseDto.class);
        log.info("Chained updated Job into Job Response: {}",jobResponse);

        return jobResponse;
    }

    private static final String UPLOAD_DIR = "uploads/";

    public String uploadCV(Long id, MultipartFile file) throws IOException, MessagingException {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or not present");
        }

        // Validate file type (assuming CV should be PDF or DOC)
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                        !contentType.equals("application/msword") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Invalid file type. Only PDF and DOC files are allowed");
        }

        // Find job
        Job job = repo.findById(id)
                .orElseThrow(() -> new JobNotFoundException("JOB NOT FOUND"));
        log.info("Job is available");

        // Check if directory exists, create if not
        Path uploadPath = Path.of(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ?
                originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        // Save the file
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());
        log.info("File saved successfully: {}", uniqueFileName);

        // Get job owner email
        User user = job.getUser();
        if (user == null) {
            throw new IllegalStateException("Job has no associated user");
        }
        String jobOwnerEmail = user.getEmail();

        // TODO: Send notification email to job owner about new CV submission
         emailService.sendCVUploadNotification(jobOwnerEmail, uniqueFileName, String.valueOf(filePath));

        return "JOB APPLIED SUCCESSFULLY. ALL THE BEST!!!";



    }

}
