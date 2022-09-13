package es.upct.cpcd.indieopen.infraestructure.tenant;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TenantFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;

		String appliedTenantId = getAppliedTenantId(request);

		if (!TenantStorage.existsTenant(appliedTenantId)) {
			log.error("Invalid tenant: " + appliedTenantId);
			HttpServletResponse response = (HttpServletResponse) res;
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			ThreadLocalStorage.setTenantName(appliedTenantId);
			filterChain.doFilter(req, res);
		}
	}

	private String getAppliedTenantId(HttpServletRequest request) {
		// INIT to Default
		String appliedTenantName = null;

		// Tenant header
		String tenantInHeader = request.getHeader("X-TenantID");

		if (request.getRequestURI().startsWith("/video/embed") || request.getRequestURI().startsWith("/course/embed")) {
			appliedTenantName = extractTenantFromEmbed(request);
		} else if (tenantInHeader != null) {
			appliedTenantName = tenantInHeader;
		}

		if (appliedTenantName == null)
			return "indieopen";

		return appliedTenantName;
	}

	private String extractTenantFromEmbed(HttpServletRequest request) {
		if (request.getParameterMap().containsKey("origin")) {
			return request.getParameter("origin");
		}

		return null;
	}

}
