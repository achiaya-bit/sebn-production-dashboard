package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DataFreshnessDTO;

/**
 * Provides dataset freshness metadata derived entirely from the database.
 */
public interface DataFreshnessService {

    /**
     * Returns freshness metadata for the current dataset.
     * All timestamp values are derived from WAO order fields (WARMDA, WARMUZ, WAAEDA).
     * The server clock is never used as a data timestamp.
     */
    DataFreshnessDTO getDataFreshness();
}
