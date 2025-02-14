/*----------------------------------------------------------------------------*/
/* Source File:   USERRECORDREDISHASHREPOSITORY.JAVA                         */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.repository;

import com.acme.service.user.domain.UserRecordRedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Uses the repository patter to access a Redis Database using Spring Data Redis.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
@Repository
public interface UserRecordRedisHashRepository extends CrudRepository<UserRecordRedisHash, String> {
}
