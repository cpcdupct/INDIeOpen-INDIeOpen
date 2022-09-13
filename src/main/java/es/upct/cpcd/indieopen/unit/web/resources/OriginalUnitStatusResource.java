package es.upct.cpcd.indieopen.unit.web.resources;

public class OriginalUnitStatusResource {

	private String lastPublished;
	private boolean existing;
	private String resource;

	public OriginalUnitStatusResource(String lastPublished, boolean existing, String resource) {
		this.lastPublished = lastPublished;
		this.existing = existing;
		this.resource = resource;
	}

	public String getLastPublished() {
		return lastPublished;
	}

	public boolean isExisting() {
		return existing;
	}

	public String getResource() {
		return resource;
	}

}
