package com.speer.authserver.repository;


import com.speer.authserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@EnableMongoRepositories
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}

