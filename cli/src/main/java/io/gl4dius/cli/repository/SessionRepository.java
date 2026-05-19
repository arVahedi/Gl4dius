package io.gl4dius.cli.repository;

import io.gl4dius.cli.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    Optional<Session> findByName(String name);

    @Query("SELECT s FROM Session s WHERE s.id = :identifier OR s.name = :identifier")
    Optional<Session> findByIdOrName(String identifier);
}
