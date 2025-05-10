package com.siemens.internship.repository;
import com.siemens.internship.model.Item;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * This interface handles all database operations related to the Item entity.
 * By extending JpaRepository, we automatically get basic CRUD methods like findAll(), save(), deleteById(), etc.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * This custom query is used to retrieve only the IDs of all items from the database.
     * It's useful when we just need to reference the items without loading all their details into memory.
     */
    @Query("SELECT i.id FROM Item i")
    List<Long> findAllIds();
}