package br.repository;

import br.entity.MarketingCampaign;
import br.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MarketingCampaignRepository
        extends JpaRepository<MarketingCampaign, UUID> {

    List<MarketingCampaign> findByTenantOrderByCriadoEmDesc(
            Tenant tenant
    );

}