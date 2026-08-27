package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.dto.OrderDTO;
import com.sebn.dashboard.dto.PagedResponse;
import com.sebn.dashboard.repository.WaoOrderRepository;
import com.sebn.dashboard.repository.WaoOrderSpecifications;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final WaoOrderRepository waoOrderRepository;

    @Override
    public PagedResponse<OrderDTO> getAllOrders(DashboardFilter filter, Pageable pageable) {
        DashboardFilter safeFilter = filter != null ? filter : DashboardFilter.empty();
        Page<OrderDTO> page = waoOrderRepository
                .findAll(WaoOrderSpecifications.withFilter(safeFilter), pageable)
                .map(OrderMapper::toDto);
        return PagedResponse.from(page);
    }

    @Override
    public OrderDTO getOrderById(Integer id) {
        return waoOrderRepository.findById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(String status) {
        return waoOrderRepository.findByWastat(status).stream()
                .map(OrderMapper::toDto)
                .toList();
    }
}
