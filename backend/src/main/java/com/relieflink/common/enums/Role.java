package com.relieflink.common.enums;

/**
 * User roles in the DRRMS system.
 *
 * <p>Maps to the MySQL ENUM('admin', 'volunteer', 'citizen') column in the {@code users} table.</p>
 */
public enum Role {
    admin,
    volunteer,
    citizen
}
