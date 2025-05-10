package com.siemens.internship.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Every item needs a name – that’s kind of the minimum we can ask for.
     * If someone tries to send in an empty name, we’ll reject it with a validation error.
     */
    @NotBlank(message = "Name cannot be blank")
    private String name;

    /**
     * Description is optional – this is just for extra info or context about the item.
     * We’re leaving it open, so clients can send it or not.
     */
    private String description;

    /**
     * We'll use this to track the item's processing status.
     * For example, it could be set to "PENDING" by default, and updated to "PROCESSED" later.
     * It's not validated here, but you could enforce allowed values if needed.
     */
    private String status;

    /**
     * Email is required and needs to be in a valid email format.
     * We use @NotBlank to make sure the field isn’t empty,
     * and @Email to ensure that what we get actually looks like a real email address.
     * Without this, we might end up saving garbage strings in the database.
     */
    @Column(nullable = false)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
}

