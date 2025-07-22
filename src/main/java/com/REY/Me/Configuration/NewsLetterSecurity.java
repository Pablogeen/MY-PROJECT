package com.REY.Me.Configuration;

import com.REY.Me.Entity.User;
import com.REY.Me.Repository.NewsLetterRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("NewsLetterSecurity")
public class NewsLetterSecurity {

    private NewsLetterRepository repo;

    public NewsLetterSecurity(NewsLetterRepository repo){
        this.repo = repo;
    }


    public boolean isNewsOwner(Authentication authentication, Long id){
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Long NewsOwnerId = repo.findNewsPostedById(id);
        return userId.equals(NewsOwnerId);
    }
}
