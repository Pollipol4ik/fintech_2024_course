package edu.kudago.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryStorageTest {

    private InMemoryStorage<String, Integer> storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage<>();
    }

    @Test
    void testSaveAndFindById() {
        String entity = "TestEntity";
        storage.save(1, entity);

        Optional<String> foundEntity = storage.findById(1);
        assertTrue(foundEntity.isPresent(), "Entity should be present");
        assertEquals(entity, foundEntity.get(), "Saved entity should match the found entity");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<String> entity = storage.findById(999);
        assertFalse(entity.isPresent(), "Entity should not be present for non-existing ID");
    }

    @Test
    void testFindAll() {

        storage.save(1, "Entity1");
        storage.save(2, "Entity2");
        storage.save(3, "Entity3");

        Iterable<String> allEntities = storage.findAll();
        assertNotNull(allEntities, "FindAll should not return null");
        assertEquals(3, ((Collection<?>) allEntities).size(), "There should be exactly 3 entities");
    }

    @Test
    void testDeleteById() {
        storage.save(1, "EntityToDelete");

        assertTrue(storage.existsById(1), "Entity should exist before deletion");

        storage.deleteById(1);

        assertFalse(storage.existsById(1), "Entity should not exist after deletion");
        assertFalse(storage.findById(1).isPresent(), "FindById should return empty after deletion");
    }

    @Test
    void testExistsById() {

        assertFalse(storage.existsById(1), "Entity should not exist initially");

        storage.save(1, "Entity");


        assertTrue(storage.existsById(1), "Entity should exist after saving");
    }

    @Test
    void testOverwriteEntity() {

        storage.save(1, "OriginalEntity");


        String newEntity = "NewEntity";
        storage.save(1, newEntity);


        Optional<String> foundEntity = storage.findById(1);
        assertTrue(foundEntity.isPresent(), "Entity should be present");
        assertEquals(newEntity, foundEntity.get(), "Entity should be overwritten with new value");
    }
}
