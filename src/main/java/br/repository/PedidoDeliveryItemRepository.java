package br.repository;

import br.entity.PedidoDeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoDeliveryItemRepository
        extends JpaRepository<PedidoDeliveryItem, UUID> {

    List<PedidoDeliveryItem> findByPedidoId(
            UUID pedidoId
    );

    long countByPedidoId(
            UUID pedidoId
    );
}