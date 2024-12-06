package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.service;

import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto.FilmDetailDto;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto.FilmDto;

import java.util.List;

public interface MaintenanceService {

    List<FilmDto> findAllFilms();

    FilmDetailDto findDetailById(Integer id);

    Boolean updateFilm(FilmDetailDto filmDetailDto);

    List<String> findAllCategoryNames();

    List<String> findAllActorNames();

    List<String> getCurrentActorNames(Integer id);

    Boolean addFilm(FilmDetailDto filmDetailDto);

    List<String> findAllLanguages();

    Boolean deleteFilm(Integer filmId);
}



