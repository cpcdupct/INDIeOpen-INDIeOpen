package es.upct.cpcd.indieopen.rate.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    private UserData author;

    @ManyToOne(optional = false)
    @JoinColumn(name = "UNIT_ID")
    private Unit unit;

    @Column(nullable = false)
    private int rating;

    public Rate() {

    }

    public Rate(UserData author, Unit unit, int rating) {
        this.author = author;
        this.unit = unit;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Rate)) {
            return false;
        }
        Rate rate = (Rate) o;
        return Objects.equals(author, rate.author) && Objects.equals(unit, rate.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, unit);
    }

}