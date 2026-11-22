package br.repository;

import br.entity.PedidoDelivery;
import br.entity.StatusPedidoDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoDeliveryRepository
        extends JpaRepository<PedidoDelivery, UUID> {

    List<PedidoDelivery> findByTenantIdOrderByDataPedidoDesc(
            UUID tenantId
    );

    Optional<PedidoDelivery> findTopByTenantIdAndSequencialIsNotNullOrderBySequencialDesc(
            UUID tenantId
    );

    Optional<PedidoDelivery> findByIdAndTenantId(
            UUID id,
            UUID tenantId
    );

    Long countByTenantIdAndStatus(
            UUID tenantId,
            StatusPedidoDelivery status
    );

    List<PedidoDelivery> findByTenantIdAndStatusOrderByDataPedidoDesc(
            UUID tenantId,
            StatusPedidoDelivery status
    );

    List<PedidoDelivery> findByTenantIdAndStatus(
            UUID tenantId,
            StatusPedidoDelivery status
    );

    Long countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(

            UUID tenantId,

            StatusPedidoDelivery status,

            LocalDateTime dataInicial

    );

    List<PedidoDelivery>
    findByTenantIdAndStatusAndDataPedidoGreaterThanEqual(

            UUID tenantId,

            StatusPedidoDelivery status,

            LocalDateTime dataInicial

    );

    List<PedidoDelivery>
    findByTenantIdAndDataPedidoGreaterThanEqualOrderByDataPedidoDesc(

            UUID tenantId,

            LocalDateTime dataInicial

    );

    List<PedidoDelivery>
    findByTenantIdAndStatusAndDataPedidoGreaterThanEqualOrderByDataPedidoDesc(

            UUID tenantId,

            StatusPedidoDelivery status,

            LocalDateTime dataInicial

    );
}
