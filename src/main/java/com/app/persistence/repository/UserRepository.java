package com.app.persistence.repository;

import com.app.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long>{

    Optional<UserEntity> findUserEntityByUsername(String username);

    /* Este custom query hace lo mismo que gindUserEntityByUsername
    @Query("SELECT u FROM UserEntity u WHERE u.usaername = ?")
    Optional<UserEntity> findUser(String username);
     */
}

