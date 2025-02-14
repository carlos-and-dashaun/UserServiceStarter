/*----------------------------------------------------------------------------*/
/* Source File:   USERRECORDREDISHASH.JAVA                                    */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/

package com.acme.service.user.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.redis.core.RedisHash;

/**
 * Represents User Record information.
 *
 * @param id        Identifies the User Record.
 * @param firstName Indicates first name (mandatory).
 * @param lastName  Indicates last name (mandatory).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "firstName", "lastName"})
@RedisHash
public record UserRecordRedisHash(
    @NotNull(message = "User 'Id' may not be null")
    @Positive(message = "User 'Id' must be positive")
    Long id,
    @NotEmpty(message = "User 'firstName' is mandatory")
    String firstName,
    @NotEmpty(message = "User 'lastName' is mandatory")
    String lastName) {
}
