package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DataFreshnessDTO;
import com.sebn.dashboard.repository.DataFreshnessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link DataFreshnessService}.
 *
 * <p>All timestamp values come from the database (WARMDA, WARMUZ, WAAEDA).
 * The server clock is never used as a data timestamp.
 *
 * <p>{@code dataMode} is controlled by the environment variable {@code DATA_MODE}
 * (default {@code IMPORTED}).  Set it to {@code LIVE} only when a real
 * WAO-to-MySQL ingestion process has been established.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataFreshnessServiceImpl implements DataFreshnessService {

    private final DataFreshnessRepository dataFreshnessRepository;

    /**
     * Injected from the {@code DATA_MODE} environment variable.
     * Defaults to {@code IMPORTED}; can be overridden to {@code LIVE}
     * once a real WAO synchronisation process exists.
     */
    @Value("${data.mode:IMPORTED}")
    private String dataMode;

    @Override
    public DataFreshnessDTO getDataFreshness() {
        String latestReportedAt         = dataFreshnessRepository.findLatestReportedAt();
        String latestOrderModification  = dataFreshnessRepository.findLatestOrderModificationDate();

        return DataFreshnessDTO.builder()
                .latestReportedAt(latestReportedAt)
                .latestOrderModificationDate(latestOrderModification)
                .dataMode(dataMode)
                .build();
    }
}
