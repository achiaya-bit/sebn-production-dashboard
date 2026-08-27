package com.sebn.dashboard.controller;

import com.sebn.dashboard.dto.OrderDTO;
import com.sebn.dashboard.dto.PagedResponse;
import com.sebn.dashboard.service.DashboardFilterFactory;
import com.sebn.dashboard.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for production order lookups.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 25;
    private static final int MAX_SIZE = 100;

    private final OrderService orderService;

    /**
     * Returns a page of production orders, optionally filtered by period, status and part number.
     */
    @GetMapping
    public ResponseEntity<PagedResponse<OrderDTO>> getAllOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        return ResponseEntity.ok(orderService.getAllOrders(
                DashboardFilterFactory.from(startDate, endDate, status, partNumber, fromTime, toTime),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "waaunr"))));
    }

    /**
     * Returns a single order by its identifier.
     *
     * @param id order primary key (must be &gt;= 1)
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable @Min(value = 1, message = "Order id must be greater than or equal to 1") Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Returns orders filtered by WASTAT status code.
     *
     * @param status non-blank status code
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @PathVariable @NotBlank(message = "Status must not be blank") String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }
}
