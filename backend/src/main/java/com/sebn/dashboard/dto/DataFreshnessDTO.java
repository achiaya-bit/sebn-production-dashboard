package com.sebn.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

/**
 * Dataset freshness metadata returned by GET /api/dashboard/data-freshness.
 *
 * <p><strong>Semantics</strong>
 * <ul>
 *   <li>{@code latestReportedAt} – ISO-8601 local datetime derived from the maximum
 *       valid {@code WARMDA} + normalised {@code WARMUZ} found in the database.
 *       It reflects the newest reporting timestamp present in the imported dataset.
 *       It does <em>not</em> imply that the application is connected live to WAO.</li>
 *   <li>{@code latestOrderModificationDate} – Maximum valid {@code WAAEDA} in
 *       {@code YYYYMMDD} format.  Null when no valid value exists.</li>
 *   <li>{@code dataMode} – {@code IMPORTED} unless the server is explicitly
 *       configured with {@code DATA_MODE=LIVE} after a real WAO synchronisation
 *       process has been established.</li>
 * </ul>
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dataset freshness metadata derived from WAO order reporting fields")
public class DataFreshnessDTO {

    @Schema(
        description = "ISO-8601 local datetime of the most recent reporting entry (WARMDA + WARMUZ). " +
                      "Derived from the database, not the server clock.",
        example = "2026-08-27T03:17:31",
        nullable = true
    )
    String latestReportedAt;

    @Schema(
        description = "Date (YYYYMMDD) of the most recently modified order (MAX WAAEDA). " +
                      "Null when no valid value exists.",
        example = "20260827",
        nullable = true
    )
    String latestOrderModificationDate;

    @Schema(
        description = "IMPORTED = data was loaded from a dataset dump. " +
                      "LIVE = backend is configured with DATA_MODE=LIVE and an active WAO sync exists.",
        example = "IMPORTED",
        allowableValues = {"IMPORTED", "LIVE"}
    )
    String dataMode;
}
