package com.sebn.dashboard.config;

import com.sebn.dashboard.controller.DashboardController;
import com.sebn.dashboard.controller.OrderController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI configuration for Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    public static final String DASHBOARD_TAG = "Dashboard";
    public static final String ORDERS_TAG = "Orders";

    /**
     * Defines the OpenAPI metadata and API tags.
     */
    @Bean
    public OpenAPI sebnProductionDashboardOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SEBN Production Dashboard API")
                        .description("REST API for Version 1 Production Dashboard")
                        .version("1.0"))
                .tags(List.of(
                        new Tag()
                                .name(DASHBOARD_TAG)
                                .description("Production KPIs, backlog and completion metrics"),
                        new Tag()
                                .name(ORDERS_TAG)
                                .description("Production order listing and lookup endpoints")));
    }

    /**
     * Associates controller operations with OpenAPI tags without annotating controllers.
     */
    @Bean
    public OperationCustomizer operationTagCustomizer() {
        return (operation, handlerMethod) -> {
            Class<?> controllerClass = handlerMethod.getBeanType();
            if (DashboardController.class.equals(controllerClass)) {
                operation.setTags(List.of(DASHBOARD_TAG));
            } else if (OrderController.class.equals(controllerClass)) {
                operation.setTags(List.of(ORDERS_TAG));
            }
            return operation;
        };
    }
}
