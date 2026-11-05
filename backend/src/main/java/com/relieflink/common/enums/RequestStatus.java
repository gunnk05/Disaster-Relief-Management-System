package com.relieflink.common.enums;

/**
 * Status lifecycle for citizen resource requests.
 *
 * <p>Flow: {@code pending → assigned → completed → fulfilled}</p>
 */
public enum RequestStatus {
    pending,
    assigned,
    completed,
    fulfilled
}
