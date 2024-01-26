package com.speer.authserver.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@NoArgsConstructor
@Document(collection = "notes")
public class Note {
    @Id
    private String noteId;
    private String authorName;
    private String subject;
    private String data;

    public Note(String authorName, String subject, String data) {
        this.authorName = authorName;
        this.subject = subject;
        this.data = data;
    }
}

