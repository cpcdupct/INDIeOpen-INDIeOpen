package es.upct.cpcd.indieopen.unit.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelToken {

	private int unitId;
	private String userId;
	private LocalDateTime expireAt;

}
