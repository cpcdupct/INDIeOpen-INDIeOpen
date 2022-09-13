package es.upct.cpcd.indieopen.token;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ModelToken {
    private final String plainToken;
    private final int entity;
    private final String user;
    private final LocalDateTime expireAt;
    private final ContentType type;

    public ModelToken(String plainToken, int entity, String user, LocalDateTime expireAt, ContentType type) {
        this.plainToken = plainToken;
        this.entity = entity;
        this.user = user;
        this.expireAt = expireAt;
        this.type = type;
    }

    public boolean isExpired() {
        return (LocalDate.now().isAfter(expireAt.toLocalDate()));
    }
}
