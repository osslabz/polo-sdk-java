package com.poloniex.api.client.spot.model.event.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Balance event
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceEvent {

    /**
     * time the change was executed
     */
    private Long changeTime;

    /**
     * account id where the change is taking place
     */
    private String accountId;

    /**
     * event type
     */
    private String eventType;

    /**
     * currency amount available
     */
    private String available;

    /**
     * currency name
     */
    private String currency;

    /**
     * id of the asset update
     */
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * currency amount on hold
     */
    private String hold;

    /**
     * time the record was pushed
     */
    private Long ts;
}
