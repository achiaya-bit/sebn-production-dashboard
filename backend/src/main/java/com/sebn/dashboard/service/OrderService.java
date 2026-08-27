package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.dto.OrderDTO;
import com.sebn.dashboard.dto.PagedResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    PagedResponse<OrderDTO> getAllOrders(DashboardFilter filter, Pageable pageable);

    OrderDTO getOrderById(Integer id);

    List<OrderDTO> getOrdersByStatus(String status);
}
