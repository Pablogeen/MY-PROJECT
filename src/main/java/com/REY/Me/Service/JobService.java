package com.REY.Me.Service;

import com.REY.Me.Entity.Job;
import com.REY.Me.Exception.JobNotFoundException;
import com.REY.Me.Repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class JobService {

    private JobRepository repo;

    public JobService(JobRepository repo){
        this.repo=repo;
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
}
