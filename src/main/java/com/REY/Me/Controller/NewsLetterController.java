package com.REY.Me.Controller;

import com.REY.Me.DTO.NewsLetterDTO;
import com.REY.Me.Entity.NewsLetter;
import com.REY.Me.Service.NewsLetterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/NewsLetter")
public class NewsLetterController {

    private NewsLetterService service;

    public NewsLetterController(NewsLetterService service){
        this.service = service;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/post")
    public ResponseEntity<String>createLetter(@RequestPart NewsLetterDTO letterDTO, @RequestPart MultipartFile imageFile) throws IOException {
        return new ResponseEntity<>(service.createNewsLetter(letterDTO, imageFile), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping()
    public ResponseEntity<List<NewsLetter>>readNewsLetter(){
            return new ResponseEntity<>(service.getNewsByDateTime(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<NewsLetter>>readNewsById(@PathVariable Long id){
        return new ResponseEntity<>(service.readNewsById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN') or @NewsLetterSecurity.isNewsOwner(authentication, #id)")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteNews(@PathVariable Long id){
        return new ResponseEntity<>(service.readNewsById(id), HttpStatus.OK);
    }



}
