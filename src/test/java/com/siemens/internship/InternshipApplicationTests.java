
package com.siemens.internship;
import com.siemens.internship.service.ItemService;
import com.siemens.internship.model.Item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * This test class exists to make sure our Spring Boot application is able to start correctly,
 * and that the core components (like the ItemService) are being wired in and functioning.
 * It's not focused on complex logic—more of a sanity check to ensure everything boots properly.
 */
@SpringBootTest
class InternshipApplicationTests {

    /**
     * We inject the ItemService here to make sure Spring can find and wire it correctly.
     * If the service is null, then something's wrong with how our components are registered.
     */
    @Autowired
    private ItemService itemService;

    /**
     * This test just ensures that the Spring context loads without crashing.
     * It also indirectly confirms that @SpringBootTest works and that @Autowired behaves as expected.
     */
    @Test
    void contextLoads() {
        assert itemService != null;
    }

    /**
     * This test checks that the ItemService returns something when we call findAll().
     * Even if the list is empty (which is fine), it should never be null.
     * That would signal a deeper issue with the repository or service layer wiring.
     */
    @Test
    void itemServiceShouldReturnList() {
        List<Item> items = itemService.findAll();
        assert items != null;
    }
}
