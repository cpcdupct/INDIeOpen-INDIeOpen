package es.upct.cpcd.indieopen.infraestructure.tenant;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantAwareRoutingSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return ThreadLocalStorage.getTenantName();
	}

}