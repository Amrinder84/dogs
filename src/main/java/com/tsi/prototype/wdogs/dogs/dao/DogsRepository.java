package com.tsi.prototype.wdogs.dogs.dao;

import com.tsi.prototype.wdogs.dogs.model.Dog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Interface represents a stub for Dogs database repository.
 */
@Repository
public interface DogsRepository extends PagingAndSortingRepository<Dog, UUID> {
    /**
     * Searches Dog entities by name
     * @param name the name of the doc
     * @return list of dogs
     */
    List<Dog> findByDogName(@NotNull String name);
}
