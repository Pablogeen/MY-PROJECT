package com.REY.Me.Service;

import com.REY.Me.DTO.NewsLetterDTO;
import com.REY.Me.Entity.NewsLetter;
import com.REY.Me.Entity.User;
import com.REY.Me.Repository.NewsLetterRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsLetterService {

  private NewsLetterRepository repo;

  public NewsLetterService(NewsLetterRepository repo){
      this.repo = repo;
  }

    public String createNewsLetter(NewsLetterDTO letterDTO, MultipartFile imageFile) throws IOException {
        NewsLetter letter = new NewsLetter();
        letter.setText(letterDTO.getText());
        letter.setImageName(imageFile.getOriginalFilename());
        letter.setImageType(imageFile.getContentType());
        letter.setImageData(imageFile.getBytes());
        letter.setTimePosted(LocalDateTime.now());
       User user = new User();
       letter.setUser(user);

       repo.save(letter);

       return "NEWS CREATED SUCCESSFULLY";


    }

    public Optional<NewsLetter> readNewsById(Long id) {
      return  repo.findById(id);
    }

    public void deleteNews(Long id){
      repo.deleteById(id);
    }

    public List<NewsLetter> getNewsByDateTime() {
      return repo.getNewsByOrderOfDate();
    }
}
