package com.speer.authserver.repository;


import com.speer.authserver.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findAllByAuthorName(String authorName);
}

