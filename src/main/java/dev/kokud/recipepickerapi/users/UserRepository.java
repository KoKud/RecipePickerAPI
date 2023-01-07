package dev.kokud.recipepickerapi.users;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface UserRepository extends ReactiveMongoRepository<User, String> {
}
