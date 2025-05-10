package com.siemens.internship.controller;
import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * This endpoint simply returns the list of all items from the database.
     * It's a basic GET request that wraps the result in a 200 OK response.
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.findAll());
    }

    /**
     * This method handles the creation of a new item.
     * We first check if the incoming item passes validation rules (like not being null, or having a valid email).
     * If any validation errors are present, we send back a 400 Bad Request along with the list of problems.
     * Otherwise, we save the item and return it with a 201 Created status.
     */
    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid item data: " + result.getAllErrors());
        }

        Item savedItem = itemService.save(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    /**
     * This is a simple lookup by ID.
     * If the item exists, we return it with 200 OK.
     * If not, we let the client know that the item wasn’t found by returning a 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        Optional<Item> itemOpt = itemService.findById(id);
        if (itemOpt.isPresent()) {
            return ResponseEntity.ok(itemOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }
    }

    /**
     * This method is used to update an existing item.
     * We first validate the input – if it fails, we immediately respond with 400 Bad Request.
     * Then we check if the item actually exists in the database.
     * If it does, we overwrite it using the same ID and return the updated version.
     * If not, we respond with a 404 so the client knows there's nothing to update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid item data: " + result.getAllErrors());
        }

        Optional<Item> existing = itemService.findById(id);
        if (existing.isPresent()) {
            item.setId(id);
            Item updated = itemService.save(item);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }
    }

    /**
     * This endpoint deletes an item by ID.
     * Before deleting, we check if it actually exists.
     * If not, we return a 404 to indicate the ID is invalid.
     * If it exists, we delete it and return a 204 No Content to indicate the action was successful without any payload.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        if (itemService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * This one it's meant to process all items asynchronously.
     * Once the processing is done (which includes updating their status to "PROCESSED"),
     * we return a list of items that were successfully handled.
     * The return type is a CompletableFuture, which lets Spring Boot handle this as a non-blocking operation.
     * We also set the response to application/json so tests can verify the format.
     */
   @GetMapping(value = "/process", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<List<Item>>> processItems() {
    return itemService.processItemsAsync()
            .thenApply(items -> ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(items));
    }


}
