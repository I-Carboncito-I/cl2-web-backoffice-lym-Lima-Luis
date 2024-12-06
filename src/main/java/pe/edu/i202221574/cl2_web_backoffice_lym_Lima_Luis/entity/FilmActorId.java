package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FilmActorId implements Serializable {
    private Integer filmId;
    private Integer actorId;

    public FilmActorId() {}

    public FilmActorId(Integer filmId, Integer actorId) {
        this.filmId = filmId;
        this.actorId = actorId;
    }

    // Getters y Setters
    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmActorId that = (FilmActorId) o;
        return Objects.equals(filmId, that.filmId) &&
                Objects.equals(actorId, that.actorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filmId, actorId);
    }
}
