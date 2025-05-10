
package com.siemens.internship;
import com.siemens.internship.model.Item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

/**
 * This test class is specifically written to verify the behavior of the /process endpoint,
 * which handles asynchronous item processing in the background.
 * The goal here is to make sure that our async flow works properly from controller to service.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ItemProcessTest {

    // MockMvc is Spring's way of simulating real HTTP requests to our endpoints, without starting a server.
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper will help us convert Java objects to JSON and back.
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * The heart of the test: we simulate an end-to-end scenario where we:
     * 1. Add a new item marked as PENDING.
     * 2. Trigger the /process endpoint which processes it asynchronously.
     * 3. Wait for the async response and check that the status was changed to PROCESSED.
     */
    @Test
    public void testProcessItemsEndpoint() throws Exception {
        // First we build an item manually. Nothing fancy, just a dummy one with PENDING status.
        Item item = new Item();
        item.setName("ToProcess");
        item.setDescription("Needs processing");
        item.setStatus("PENDING");
        item.setEmail("valid@email.com");

        // We send this item to the server using a POST request.
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated()); // We expect the server to acknowledge creation with HTTP 201

        // Now we simulate a GET to /process. This kicks off the async processing logic.
        // Because the method is marked as @Async, we must use async expectations.
        MvcResult mvcResult = mockMvc.perform(get("/api/items/process"))
                .andExpect(request().asyncStarted()) // We confirm the request is indeed handled asynchronously
                .andReturn();

        // Here’s the trick: asyncDispatch tells Spring to wait for the async job to finish before evaluating the response.
        mockMvc.perform(asyncDispatch(mvcResult))
                .andDo(print()) // This just prints the raw HTTP exchange to the console (for debug)
                .andExpect(status().isOk()) // We expect 200 OK
                .andExpect(jsonPath("$").isArray()) // The response should be a JSON array
                .andExpect(jsonPath("$[0].status").value("PROCESSED")); // The first item's status should now be PROCESSED
    }
}
