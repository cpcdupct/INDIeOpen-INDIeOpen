package es.upct.cpcd.indieopen.media.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import es.upct.cpcd.indieopen.utils.ObjectUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entity that represents a link between a UPCT Media Resource, a Unit and a
 * User.
 */
@Getter
@NoArgsConstructor
@Entity
@IdClass(MediaKey.class)
public class MediaLink {

    @Id
    @Column(length = 36)
    private String resource;

    @Id
    private int unit;

    @Id
    @Column(length = 40)
    private String user;

    private MediaLink(String resource, int unit, String user) {
        this.resource = resource;
        this.unit = unit;
        this.user = user;
    }

    /**
     * Creates a MediaLink with a resource, unit and user identifiers
     *
     * @param resource Resource identifier
     * @param unit     Unit identifier
     * @param user     User identifier
     * @return MediaLink instance
     */
    public static MediaLink create(String resource, int unit, String user) {
        ObjectUtils.requireStringValid(resource);
        return new MediaLink(resource, unit, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MediaLink)) {
            return false;
        }

        MediaLink media = (MediaLink) o;
        return (Objects.deepEquals(resource, media.getResource()) && unit == media.getUnit()
                && user.equals(media.getUser()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(resource, unit, user);
    }
}