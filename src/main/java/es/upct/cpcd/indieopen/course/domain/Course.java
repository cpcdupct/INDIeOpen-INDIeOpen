package es.upct.cpcd.indieopen.course.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ModelUtils;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Course implements Serializable {
	private static final long serialVersionUID = "MY_SERIAL_VERSION";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(optional = false)
	@JoinColumn(name = "AUTHOR_ID")
	private UserData author;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column()
	private LocalDateTime publishedAt;

	@Column(nullable = false)
	private String documentID;

	@Column(nullable = false, unique = true, length = 32)
	private String resourceId;

	@Column(unique = true, length = 36)
	private String externalID;

	@Column()
	private LocalDateTime publishedDate;

	@Column(nullable = false)
	private boolean draft;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "course", orphanRemoval = true)
	private List<CourseAccess> authorizedAccesses;

	public Course() {
		this.createdAt = LocalDateTime.now();
		this.resourceId = ModelUtils.randomUUID(true);
		this.externalID = ModelUtils.randomUUID(true);
	}

	public boolean isPublished() {
		return (externalID != null && publishedAt != null);
	}

	public void publishCourse() {
		publishedAt = LocalDateTime.now();
		draft = false;
	}

}