package es.upct.cpcd.indieopen.video.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ModelUtils;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Video implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private String documentID;

	@Column(nullable = false)
	private String videoURL;

	@Column(unique = true, length = 36)
	private String externalID;

	@Column()
	private LocalDateTime publishedAt;

	@Column(nullable = false)
	private boolean draft;

	@ManyToOne(optional = false)
	@JoinColumn(name = "AUTHOR_ID")
	private UserData author;

	public Video() {
		this.createdAt = LocalDateTime.now();
		this.externalID = ModelUtils.randomUUID(true);
	}

	public boolean isPublished() {
		return (externalID != null && publishedAt != null);
	}

	public void publishVideo() {
		publishedAt = LocalDateTime.now();
		draft = false;
	}
}