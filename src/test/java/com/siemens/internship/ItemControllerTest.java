
package com.siemens.internship;
import com.siemens.internship.model.Item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * This test sends a POST request to create a new item.
     * It verifies that the response status is 201 Created,
     * and that the returned JSON contains the correct fields.
     */
    @Test
    public void testCreateItemSuccessfully() throws Exception {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("This is a test item.");
        item.setStatus("PENDING");
        item.setEmail("test@example.com");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    /**
     * This test tries to create an item with an invalid email format.
     * We expect this to fail because our model requires a valid email using @Email.
     * The controller should respond with 400 Bad Request and a clear error message.
     */
    @Test
    public void testCreateItemWithInvalidEmail() throws Exception {
        Item item = new Item();
        item.setName("Invalid Email Item");
        item.setDescription("Desc");
        item.setStatus("PENDING");
        item.setEmail("not-an-email");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email must be valid")));
    }

    /**
     * Here we test fetching an item that actually exists.
     * First we create it, then we grab its ID and perform a GET on it.
     * We expect the API to return the item along with status 200 OK.
     */
    @Test
    public void testGetItemByIdFound() throws Exception {
        Item item = new Item();
        item.setName("Sample");
        item.setDescription("Desc");
        item.setStatus("PENDING");
        item.setEmail("sample@example.com");

        Item saved = objectMapper.readValue(mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andReturn().getResponse().getContentAsString(), Item.class);

        mockMvc.perform(get("/api/items/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    /**
     * This one checks what happens when we try to fetch an item that doesn’t exist.
     * We're using a made-up ID (e.g., 99999) and expecting a 404 Not Found response.
     * It's important for clients to know when resources aren’t available.
     */
    @Test
    public void testGetItemByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/items/99999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Item not found")));
    }

    /**
     * In this test we create an item and then immediately try to update it.
     * The goal is to confirm the update endpoint works and the changes persist correctly.
     * After the PUT request, we verify that the name has changed in the response.
     */
    @Test
    public void testUpdateItemValid() throws Exception {
        Item item = new Item();
        item.setName("Initial");
        item.setDescription("To be updated");
        item.setStatus("PENDING");
        item.setEmail("update@example.com");

        Item saved = objectMapper.readValue(mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andReturn().getResponse().getContentAsString(), Item.class);

        saved.setName("Updated Name");

        mockMvc.perform(put("/api/items/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    /**
     * This test ensures the delete functionality is working as expected.
     * We create an item, delete it, and then check if the response is 204 No Content.
     * This is how we confirm the item was really removed from the system.
     */
    @Test
    public void testDeleteItem() throws Exception {
        Item item = new Item();
        item.setName("To Delete");
        item.setDescription("Desc");
        item.setStatus("PENDING");
        item.setEmail("delete@example.com");

        Item saved = objectMapper.readValue(mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andReturn().getResponse().getContentAsString(), Item.class);

        mockMvc.perform(delete("/api/items/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
