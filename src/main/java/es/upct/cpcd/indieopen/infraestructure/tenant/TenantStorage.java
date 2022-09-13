package es.upct.cpcd.indieopen.infraestructure.tenant;

import java.util.LinkedList;
import java.util.List;

import es.upct.cpcd.indieopen.utils.StringUtils;

public class TenantStorage {
	private static List<String> tenantNames;

	static {
		tenantNames = new LinkedList<String>();
	}

	private TenantStorage() {

	}

	public static List<String> getTenantNames() {
		return tenantNames;
	}

	public static void setTenantNames(List<String> tenantNames) {
		TenantStorage.tenantNames = tenantNames;
	}

	public static boolean existsTenant(String name) {
		if (StringUtils.isNullOrEmpty(name))
			return false;

		return tenantNames.contains(name);
	}

}
