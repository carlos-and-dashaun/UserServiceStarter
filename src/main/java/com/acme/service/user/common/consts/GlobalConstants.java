/*----------------------------------------------------------------------------*/
/* Source File:   GLOBALCONSTANTS.JAVA                                        */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.common.consts;

/**
 * General purpose application constants.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
public class GlobalConstants {
    public static final String USER_CONTROLLER_GET_RETRIEVE_USERS_INFO = "GET api/v1/users -> Retrieving Users";
    public static final String USER_CONTROLLER_POST_INSERT_USER_INFO = "POST api/v1/users -> Create user.";
    public static final String USER_CONTROLLER_GET_RETRIEVE_USER_INFO = "GET api/v1/users/{userId} -> Retrieve user.";
    public static final String USER_CONTROLLER_PATCH_USER_INFO = "PATCH api/v1/users -> Update user.";
    public static final String USER_CONTROLLER_DELETE_USER_INFO = "DELETE api/v1/users/{userId} -> Remove user.";
    public static final String COLON_SPACE_DELIMITER = ": ";
    public static final String DOT = ".";
    public static final String WILD_CARD_ASTERISK = "*";
    public static final String EMPTY_STRING = "";
    public static final String USER_RECORD_CACHE_KEY = "USER";

    public static final int INT_ZERO = 0;
    public static final int INT_ONE = 1;
    public static final int INT_TWO = 2;
    public static final int INT_THREE = 3;

    public static final long LONG_ZERO = 0L;
    public static final long LONG_ONE = 1L;
    public static final long LONG_TWO = 2L;
    public static final long LONG_THREE = 3L;

    /**
     * Utility class, thus no constructor allowed.
     */
    private GlobalConstants() {
    }
}
