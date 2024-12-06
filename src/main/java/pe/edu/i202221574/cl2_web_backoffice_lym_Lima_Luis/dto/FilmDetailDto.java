package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto;


import java.util.Date;
import java.util.List;

public record FilmDetailDto(
        Integer filmId,
        String title,
        String description,
        Integer releaseYear,
        Integer rentalDuration,
        Double rentalRate,
        Integer length,
        Double replacementCost,
        String rating,
        String specialFeatures,
        Date lastUpdate,
        String languageName,
        List<String> categories,
        List<String> actors
) {}







