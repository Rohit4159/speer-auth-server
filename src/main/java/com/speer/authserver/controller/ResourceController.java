package com.speer.authserver.controller;

import com.speer.authserver.model.Note;
import com.speer.authserver.model.User;
import com.speer.authserver.repository.NoteRepository;
import com.speer.authserver.repository.UserRepository;
import com.speer.authserver.util.JwtAuthenticationFilter;
import com.speer.authserver.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/speer")
@Slf4j
public class ResourceController {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public ResourceController(UserRepository userRepository, NoteRepository noteRepository, JwtTokenProvider tokenProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @PostMapping("/addNote")
    public ResponseEntity<String> addNote(@RequestBody Note note, HttpServletRequest request) {
        String jwt = jwtAuthenticationFilter.extractJwtFromRequest(request);
        String tokenUserId = tokenProvider.getUserIdFromToken(jwt);

        // Compare the user from the token with the authorName from the Note
        if (!tokenUserId.equals(note.getAuthorName())) {
            return new ResponseEntity<>("Unauthorized: Token does not match the provided authorName", HttpStatus.UNAUTHORIZED);
        }
        Optional<User> userOptional = userRepository.findByUsername(note.getAuthorName());

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("No such user exist! " +
                    "make sure you are are registered with us", HttpStatus.BAD_REQUEST);
        }
        noteRepository.save(note);
        return new ResponseEntity<>("Noted!!", HttpStatus.ACCEPTED);
    }

    @GetMapping("/allNotes/{authorName}")
    public ResponseEntity<List<Note>> getAllNotesForTheUser(@PathVariable String authorName, HttpServletRequest request) {
        String jwt = jwtAuthenticationFilter.extractJwtFromRequest(request);
        String tokenUserId = tokenProvider.getUserIdFromToken(jwt);

        // Compare the user from the token with the authorName from the Note
        if (!tokenUserId.equals(authorName)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        List<Note> allNotes = null;
        Optional<User> userOptional = userRepository.findByUsername(authorName);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        allNotes = noteRepository.findAllByAuthorName(authorName);
        return new ResponseEntity<>(allNotes, HttpStatus.ACCEPTED);
    }

}
