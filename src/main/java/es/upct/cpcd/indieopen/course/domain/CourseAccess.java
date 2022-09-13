package es.upct.cpcd.indieopen.course.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CourseAccess implements Serializable {
	private static final long serialVersionUID = "MY_SERIAL_VERSION";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	private Course course;

	@Column(nullable = false)
	private String email;

	CourseAccess() {

	}

	public CourseAccess(Course course, String email) {
		this.course = course;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public Course getCourse() {
		return course;
	}

	public String getEmail() {
		return email;
	}

}
