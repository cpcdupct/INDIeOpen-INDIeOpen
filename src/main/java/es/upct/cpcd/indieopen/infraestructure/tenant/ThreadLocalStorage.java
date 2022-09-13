package es.upct.cpcd.indieopen.infraestructure.tenant;

public class ThreadLocalStorage {
	private static ThreadLocal<String> TENANT = new ThreadLocal<>();

	private ThreadLocalStorage() {

	}

	public static void setTenantName(String tenantName) {
		TENANT.set(tenantName);
	}

	public static String getTenantName() {
		return TENANT.get();
	}

	public void unload() {
		TENANT.remove();
	}

}
