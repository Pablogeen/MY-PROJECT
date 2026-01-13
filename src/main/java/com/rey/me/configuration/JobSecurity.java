package com.rey.me.configuration;

import com.rey.me.entity.User;
import com.rey.me.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component("JobSecurity")
@RequiredArgsConstructor
public class JobSecurity {

    private final JobRepository repo;

    public boolean isJobOwner(@AuthenticationPrincipal User user, Long id){
        Long userId = user.getId();
        Long jobOwnerId = repo.findUserByJobId(id);
        return userId.equals(jobOwnerId);
    }

}
