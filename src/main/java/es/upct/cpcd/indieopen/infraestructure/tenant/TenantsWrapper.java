package es.upct.cpcd.indieopen.infraestructure.tenant;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Tenants")
@XmlAccessorType(XmlAccessType.FIELD)
public class TenantsWrapper {

	@XmlElement(name = "Tenant")
	private List<Tenant> tenants;

	TenantsWrapper() {

	}

	public TenantsWrapper(List<Tenant> tenants) {
		this.tenants = tenants;
	}

	public List<Tenant> getTenants() {
		return tenants;
	}

	@Override
	public String toString() {
		return "TenantsWrapper [tenants=" + tenants + "]";
	}

}
