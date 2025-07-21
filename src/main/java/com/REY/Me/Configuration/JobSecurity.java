package com.REY.Me.Configuration;

import com.REY.Me.Entity.User;
import com.REY.Me.Repository.JobRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("JobSecurity")
public class JobSecurity {

    private JobRepository repo;

    public JobSecurity(JobRepository repo){
        this.repo =repo;
    }

    public boolean isJobOwner(Authentication authentication, Long id){
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Long jobOwnerId = repo.findUserByJobId(id);
        return userId.equals(jobOwnerId);

    }

}
