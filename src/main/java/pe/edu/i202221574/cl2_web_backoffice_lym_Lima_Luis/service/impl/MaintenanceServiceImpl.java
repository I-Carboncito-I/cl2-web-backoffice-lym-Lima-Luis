package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.service.impl;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto.*;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.entity.*;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto.FilmDetailDto;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.repository.*;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.service.MaintenanceService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    private EntityManager entityManager;


    @Override
    public List<String> findAllLanguages() {
        return StreamSupport.stream(languageRepository.findAll().spliterator(), false)
                .map(Language::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllCategoryNames() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllActorNames() {
        return StreamSupport.stream(actorRepository.findAll().spliterator(), false)
                .map(actor -> actor.getFirstName() + " " + actor.getLastName())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getCurrentActorNames(Integer filmId) {
        // Buscar la película por ID
        Optional<Film> filmOptional = filmRepository.findById(filmId);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();

            // Obtener los actores actuales asociados a la película
            return film.getFilmActors().stream()
                    .map(filmActor -> filmActor.getActor().getFirstName() + " " + filmActor.getActor().getLastName())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    @Cacheable(value = "films", unless = "#result == null")
    @Override
    public List<FilmDto> findAllFilms() {
        List<FilmDto> films = new ArrayList<FilmDto>();
        Iterable<Film> iterable = filmRepository.findAll();
        iterable.forEach(film -> {
            FilmDto filmDto = new FilmDto(film.getFilmId(),
                    film.getTitle(),
                    film.getLanguage().getName(),
                    film.getRentalDuration(),
                    film.getRentalRate());
            films.add(filmDto);
        });
        return films;
    }

    @Cacheable(value = "films", unless = "#result == null")
    @Override
    public FilmDetailDto findDetailById(Integer id) {
        return filmRepository.findById(id)
                .map(film -> new FilmDetailDto(
                        film.getFilmId(),
                        film.getTitle(),
                        film.getDescription(),
                        film.getReleaseYear(),
                        film.getRentalDuration(),
                        film.getRentalRate(),
                        film.getLength(),
                        film.getReplacementCost(),
                        film.getRating(),
                        film.getSpecialFeatures(),
                        film.getLastUpdate(),
                        film.getLanguage() != null ? film.getLanguage().getName() : "",
                        film.getFilmCategories() != null ? film.getFilmCategories().stream()
                                .map(fc -> fc.getCategory().getName())
                                .collect(Collectors.toList()) : new ArrayList<>(),
                        film.getFilmActors() != null ? film.getFilmActors().stream()
                                .map(fa -> fa.getActor().getFirstName() + " " + fa.getActor().getLastName())
                                .collect(Collectors.toList()) : new ArrayList<>()
                ))
                .orElse(null);
    }

    @Cacheable(value = "films", unless = "#result == null")
    @Override
    public Boolean updateFilm(FilmDetailDto filmDetailDto) {
        return filmRepository.findById(filmDetailDto.filmId()).map(film -> {
            // Actualizar campos básicos de la película
            film.setTitle(filmDetailDto.title());
            film.setDescription(filmDetailDto.description());
            film.setReleaseYear(filmDetailDto.releaseYear());
            film.setRentalDuration(filmDetailDto.rentalDuration());
            film.setRentalRate(filmDetailDto.rentalRate());
            film.setLength(filmDetailDto.length());
            film.setReplacementCost(filmDetailDto.replacementCost());
            film.setRating(filmDetailDto.rating());
            film.setSpecialFeatures(filmDetailDto.specialFeatures());
            film.setLastUpdate(new Date());


            List<String> categories = filmDetailDto.categories() != null ? filmDetailDto.categories() : new ArrayList<>();
            List<String> actors = filmDetailDto.actors() != null ? filmDetailDto.actors() : new ArrayList<>();

            // Obtener las categorías y actores actuales en la película
            List<String> currentCategories = film.getFilmCategories().stream()
                    .map(fc -> fc.getCategory().getName())
                    .collect(Collectors.toList());
            List<String> currentActors = film.getFilmActors().stream()
                    .map(fa -> fa.getActor().getFirstName() + " " + fa.getActor().getLastName())
                    .collect(Collectors.toList());

            if (!currentCategories.equals(categories)) {
                // Limpiar categorías actuales
                film.getFilmCategories().clear();
                // Agregar nuevas categorías
                List<Category> allCategories = StreamSupport
                        .stream(categoryRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());

                for (String categoryName : categories) {
                    allCategories.stream()
                            .filter(c -> c.getName().equalsIgnoreCase(categoryName.trim()))
                            .findFirst()
                            .ifPresent(category -> {
                                FilmCategory filmCategory = new FilmCategory();
                                filmCategory.setId(new FilmCategoryId(film.getFilmId(), category.getCategoryId()));
                                filmCategory.setFilm(film);
                                filmCategory.setCategory(category);
                                filmCategory.setLastUpdate(new Date());
                                film.getFilmCategories().add(filmCategory);
                            });
                }
            }

            if (!currentActors.equals(actors)) {

                // Agregar nuevos actores
                List<Actor> allActors = StreamSupport
                        .stream(actorRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());

                for (String actorName : actors) {
                    allActors.stream()
                            .filter(a -> (a.getFirstName() + " " + a.getLastName()).equalsIgnoreCase(actorName.trim()))
                            .findFirst()
                            .ifPresent(actor -> {
                                FilmActor filmActor = new FilmActor();
                                filmActor.setId(new FilmActorId(film.getFilmId(), actor.getActorId()));
                                filmActor.setFilm(film);
                                filmActor.setActor(actor);
                                filmActor.setLastUpdate(new Date());
                                film.getFilmActors().add(filmActor);
                            });
                }
            }

            // Guardar la película actualizada
            filmRepository.save(film);
            return true;
        }).orElse(false);
    }


    @Cacheable(value = "films", unless = "#result == null")
    @Override
    public Boolean addFilm(FilmDetailDto filmDetailDto) {
        try {
            // Crear una nueva instancia de Film
            Film film = new Film();
            film.setTitle(filmDetailDto.title());
            film.setDescription(filmDetailDto.description());
            film.setReleaseYear(filmDetailDto.releaseYear());
            film.setRentalDuration(filmDetailDto.rentalDuration());
            film.setRentalRate(filmDetailDto.rentalRate());
            film.setLength(filmDetailDto.length());
            film.setReplacementCost(filmDetailDto.replacementCost());
            film.setRating(filmDetailDto.rating());
            film.setSpecialFeatures(filmDetailDto.specialFeatures());
            film.setLastUpdate(new Date());


            Map<String, Language> languageMap = StreamSupport
                    .stream(languageRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(
                            lang -> lang.getName().toLowerCase().trim(),
                            lang -> lang
                    ));

            // Validar idioma
            Language language = languageMap.get(filmDetailDto.languageName().toLowerCase().trim());
            if (language == null) {
                throw new IllegalArgumentException("El idioma especificado no existe: " + filmDetailDto.languageName());
            }
            film.setLanguage(language);

            film.setFilmCategories(new ArrayList<>());
            film.setFilmActors(new ArrayList<>());


            Map<String, Category> categoryMap = StreamSupport
                    .stream(categoryRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(
                            category -> category.getName().toLowerCase().trim(),
                            category -> category
                    ));

            // Asignar categorías
            for (String categoryName : filmDetailDto.categories()) {
                Category category = categoryMap.get(categoryName.toLowerCase().trim());
                if (category == null) {
                    throw new IllegalArgumentException("Categoría no encontrada: " + categoryName);
                }
                FilmCategory filmCategory = new FilmCategory();
                filmCategory.setId(new FilmCategoryId(film.getFilmId(), category.getCategoryId()));
                filmCategory.setFilm(film);
                filmCategory.setCategory(category);
                filmCategory.setLastUpdate(new Date());
                film.getFilmCategories().add(filmCategory);
            }

            Map<Integer, Actor> actorMap = StreamSupport
                    .stream(actorRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(
                            actor -> actor.getActorId(),
                            actor -> actor
                    ));

            // Asignar actores
            for (String actorName : filmDetailDto.actors()) {
                String[] parts = actorName.split(" ");
                String firstName = parts[0];
                String lastName = parts.length > 1 ? parts[1] : "";

                Optional<Actor> actorOpt = actorMap.values().stream()
                        .filter(actor -> actor.getFirstName().equalsIgnoreCase(firstName) &&
                                actor.getLastName().equalsIgnoreCase(lastName))
                        .findFirst();

                if (actorOpt.isPresent()) {
                    Actor actor = actorOpt.get();
                    FilmActor filmActor = new FilmActor();
                    filmActor.setId(new FilmActorId(film.getFilmId(), actor.getActorId()));
                    filmActor.setFilm(film);
                    filmActor.setActor(actor);
                    filmActor.setLastUpdate(new Date());
                    film.getFilmActors().add(filmActor);
                } else {
                    throw new IllegalArgumentException("Actor no encontrado: " + actorName);
                }
            }

            // Guardar película
            filmRepository.save(film);
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar la película: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Cacheable(value = "films", unless = "#result == null")
    @Transactional
    public Boolean deleteFilm(Integer filmId) {
        try {
            // Mensaje antes de eliminar
            System.out.println("Eliminando película con id: " + filmId);

            entityManager.createNativeQuery("DELETE FROM rental WHERE inventory_id IN (SELECT inventory_id FROM inventory WHERE film_id = :filmId)")
                    .setParameter("filmId", filmId)
                    .executeUpdate();

            // Luego, eliminamos el film
            filmRepository.deleteById(filmId);

            // Mensaje después de eliminar
            System.out.println("Película con id: " + filmId + " eliminada exitosamente.");
            return true;
        } catch (Exception e) {
            // Mensaje de error
            System.out.println("Error al eliminar la película con id: " + filmId);
            e.printStackTrace();
            return false;
        }
    }
}










