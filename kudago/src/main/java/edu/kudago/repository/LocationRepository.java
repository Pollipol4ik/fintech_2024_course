package edu.kudago.repository;


import edu.kudago.repository.entity.LocationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    @EntityGraph(attributePaths = {"events"})
    @Query("SELECT l FROM LocationEntity l LEFT JOIN FETCH l.events WHERE l.id = :id")
    Optional<LocationEntity> findByIdWithEvents(Long id);
}
