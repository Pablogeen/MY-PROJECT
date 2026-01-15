package com.rey.me.configuration;

import com.rey.me.entity.User;
import com.rey.me.repository.NewsLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component("NewsLetterSecurity")
@RequiredArgsConstructor
public class NewsLetterSecurity {

    private final NewsLetterRepository letterRepo;

    public boolean isNewsOwner(@AuthenticationPrincipal User user, Long id){
        Long userId = user.getId();
        Long NewsOwnerId = letterRepo.findUserByNewsLetter(id);
        return userId.equals(NewsOwnerId);
    }
}
