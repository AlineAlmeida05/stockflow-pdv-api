package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.MarketingCampaign;
import br.com.stockflow.stockflow_api.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MarketingCampaignRepository
        extends JpaRepository<MarketingCampaign, UUID> {

    List<MarketingCampaign> findByTenantOrderByCriadoEmDesc(
            Tenant tenant
    );

}