package es.upct.cpcd.indieopen.course.bean;

import javax.validation.constraints.NotBlank;

public class CourseBean {

	@NotBlank
	private String name;

	CourseBean() {

	}

	public CourseBean(@NotBlank String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
