package es.upct.cpcd.indieopen.infraestructure;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariDataSource;

import es.upct.cpcd.indieopen.infraestructure.tenant.Tenant;
import es.upct.cpcd.indieopen.infraestructure.tenant.TenantAwareRoutingSource;
import es.upct.cpcd.indieopen.infraestructure.tenant.TenantStorage;
import es.upct.cpcd.indieopen.infraestructure.tenant.TenantsWrapper;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class TenantConfig {

	private static final String TENANT_FOLDER = "tenants/";
	private TenantsWrapper tenants;
	private Environment environment;

	@Autowired
	public TenantConfig(Environment environment) {
		this.environment = environment;
		populateTenants();
	}

	private void populateTenants() {
		try {
			JAXBContext jc = JAXBContext.newInstance(TenantsWrapper.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			log.info("LOADING TENANT FILE: " + TENANT_FOLDER + environment.getProperty("tenant.file"));

			this.tenants = (TenantsWrapper) unmarshaller.unmarshal(getTenantsFile());
			TenantStorage.setTenantNames(
					this.tenants.getTenants().stream().map(Tenant::getName).collect(Collectors.toList()));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private InputStreamReader getTenantsFile() throws IOException {
		ClassPathResource resource = new ClassPathResource(TENANT_FOLDER + environment.getProperty("tenant.file"));
		URL url = resource.getURL();
		return new InputStreamReader(url.openStream());
	}

	@Bean
	public DataSource dataSource() {
		AbstractRoutingDataSource dataSourceConfiguration = new TenantAwareRoutingSource();
		Map<Object, Object> targetDataSources = getTenantsMap();

		dataSourceConfiguration.setTargetDataSources(targetDataSources);
		dataSourceConfiguration.setDefaultTargetDataSource(getDefaultTenant());
		dataSourceConfiguration.afterPropertiesSet();

		return dataSourceConfiguration;
	}

	private DataSource getDefaultTenant() {
		return getDataSourceFromTenant(this.tenants.getTenants().stream().filter(t -> "indieopen".equals(t.getName()))
				.findFirst().orElseThrow(() -> new IllegalStateException("No default tenant is set")));
	}

	private Map<Object, Object> getTenantsMap() {
		Map<Object, Object> map = new HashMap<>();

		for (Tenant tenant : this.tenants.getTenants())
			map.put(tenant.getName(), getDataSourceFromTenant(tenant));

		return map;
	}

	private DataSource getDataSourceFromTenant(Tenant tenant) {
		HikariDataSource dataSource = new HikariDataSource();

		// DEFAULTS
		dataSource.setInitializationFailTimeout(0);
		dataSource.setMaximumPoolSize(5);
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setMaxLifetime(60000);
		dataSource.setConnectionTestQuery("SELECT 1");

		// TENANT DEPENDENT
		dataSource.setJdbcUrl(tenant.getJdbcurl());
		dataSource.setUsername(tenant.getUser());
		dataSource.setPassword(tenant.getPassword());

		return dataSource;
	}

}
