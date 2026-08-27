package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DataFreshnessDTO;
import com.sebn.dashboard.repository.DataFreshnessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DataFreshnessServiceImpl}.
 * No Spring context or database required — the repository is mocked.
 */
@ExtendWith(MockitoExtension.class)
class DataFreshnessServiceTest {

    @Mock
    private DataFreshnessRepository dataFreshnessRepository;

    @InjectMocks
    private DataFreshnessServiceImpl service;

    /** Inject the @Value field without a Spring context. */
    private void setDataMode(String mode) {
        ReflectionTestUtils.setField(service, "dataMode", mode);
    }

    // ── latestReportedAt ──────────────────────────────────────────────────

    @Test
    void latestReportedAt_fromRepository_isReturnedInDto() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn("2026-08-27T05:17:31");
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn("20260827");

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getLatestReportedAt()).isEqualTo("2026-08-27T05:17:31");
    }

    @Test
    void latestReportedAt_null_isReturnedAsNull() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn(null);
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn(null);

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getLatestReportedAt()).isNull();
    }

    // ── latestOrderModificationDate ───────────────────────────────────────

    @Test
    void latestOrderModificationDate_fromRepository_isReturnedInDto() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn(null);
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn("20260827");

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getLatestOrderModificationDate()).isEqualTo("20260827");
    }

    @Test
    void latestOrderModificationDate_null_isReturnedAsNull() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn(null);
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn(null);

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getLatestOrderModificationDate()).isNull();
    }

    // ── dataMode ──────────────────────────────────────────────────────────

    @Test
    void dataMode_imported_isReturnedInDto() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn(null);
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn(null);

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getDataMode()).isEqualTo("IMPORTED");
    }

    @Test
    void dataMode_live_isReturnedInDto() {
        // dataMode=LIVE is only set when a real WAO sync process exists;
        // here we verify the service passes the value through without modification.
        setDataMode("LIVE");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn("2026-08-27T14:00:00");
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn("20260827");

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getDataMode()).isEqualTo("LIVE");
    }

    // ── full DTO assembly ─────────────────────────────────────────────────

    @Test
    void allFields_populatedFromRepository() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn("2026-08-27T13:45:09");
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn("20260827");

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getLatestReportedAt()).isEqualTo("2026-08-27T13:45:09");
        assertThat(dto.getLatestOrderModificationDate()).isEqualTo("20260827");
        assertThat(dto.getDataMode()).isEqualTo("IMPORTED");
    }

    @Test
    void allFieldsNull_whenRepositoryReturnsNull() {
        setDataMode("IMPORTED");
        when(dataFreshnessRepository.findLatestReportedAt()).thenReturn(null);
        when(dataFreshnessRepository.findLatestOrderModificationDate()).thenReturn(null);

        DataFreshnessDTO dto = service.getDataFreshness();

        assertThat(dto.getLatestReportedAt()).isNull();
        assertThat(dto.getLatestOrderModificationDate()).isNull();
        assertThat(dto.getDataMode()).isEqualTo("IMPORTED");
    }
}
