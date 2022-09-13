package es.upct.cpcd.indieopen.infraestructure.tenant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Tenant {

	@XmlAttribute
	private String name;

	private String jdbcurl;
	private String user;
	private String password;

	Tenant() {

	}

	public Tenant(String name, String jdbcurl, String user, String password) {
		this.name = name;
		this.jdbcurl = jdbcurl;
		this.user = user;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getJdbcurl() {
		return jdbcurl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "Tenant [name=" + name + ", jdbcurl=" + jdbcurl + ", user=" + user + ", password=" + password + "]";
	}

}
