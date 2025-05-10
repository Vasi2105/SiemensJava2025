package com.siemens.internship.service;
import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Item> processedItems = new ArrayList<>();
    private int processedCount = 0;


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */




   /**
     * This methd is responsible for asynchronously processing every item in the database.
     * The idea is to fetch all item IDs first, then process each of them in parallel threads.
     * While processing, we mrk each item as "PROCESSED" and save the updated version.
     * At the end, we collect all succesfully processed items into a list and return themm
     * as a CompletableFuture, so that the controller can await the result when it's ready.
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {

        // This thread-safe list will hold all items that were successfully processed.
        List<Item> successfullyProcessed = new CopyOnWriteArrayList<>();

        // We start by fetching all the item IDs from the database to work with.
        List<Long> itemIds = itemRepository.findAllIds();

        // We prepare a list to hold all the asynchronous tasks (futures).
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Long id : itemIds) {
            // For each ID, we create a new asynchronous task to process the item.
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // Try to find the item from the DB – if it doesn't exist, we skip it.
                    Optional<Item> optionalItem = itemRepository.findById(id);
                    if (optionalItem.isPresent()) {
                        Item item = optionalItem.get();

                        // Simulate some processing time (not ideal in real world, but ok for demo)
                        Thread.sleep(100);

                        // Mark the item as processed and save it back to the DB
                        item.setStatus("PROCESSED");
                        itemRepository.save(item);

                        // Add it to our results list
                        successfullyProcessed.add(item);
                    }
                } catch (Exception e) {
                    // If something goes wrong (e.g., DB issue), we log it to help debugging
                    System.err.println("Failed to process item with ID " + id + ": " + e.getMessage());
                }
            }, executor);

            // Keep track of the task so we can wait for all to complete later
            futures.add(future);
        }

        // This part makes sure we wait until ALL tasks are fullly completed.
        // Only after that we return the list of processed items.
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(voidResult -> successfullyProcessed);
    }
}


