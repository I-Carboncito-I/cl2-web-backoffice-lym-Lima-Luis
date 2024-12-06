package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto.FilmDetailDto;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.dto.FilmDto;
import pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.service.MaintenanceService;


import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    MaintenanceService maintenanceService;


    @Cacheable(value = "films", unless = "#result == null")
    @GetMapping("/start")
    public String start(Model model) {
        List<FilmDto> films = maintenanceService.findAllFilms();
        model.addAttribute("films", films);
        return "maintenance";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        FilmDetailDto filmDetailDto = maintenanceService.findDetailById(id);
        model.addAttribute("film", filmDetailDto);
        return "maintenance-detail";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        FilmDetailDto filmDetailDto = maintenanceService.findDetailById(id);

        // Obtener las listas de categorías, actores disponibles y actores actuales
        List<String> categories = maintenanceService.findAllCategoryNames();
        List<String> actorNames = maintenanceService.findAllActorNames();
        List<String> currentActors = maintenanceService.getCurrentActorNames(id);

        // Lista de clasificaciones
        List<String> ratings = List.of("G", "PG", "PG-13", "R", "NC-17");


        if (currentActors == null) {
            currentActors = new ArrayList<>();
        }
        if (actorNames == null) {
            actorNames = new ArrayList<>();
        }

        if (filmDetailDto.actors() == null) {
            filmDetailDto = new FilmDetailDto(
                    filmDetailDto.filmId(),
                    filmDetailDto.title(),
                    filmDetailDto.description(),
                    filmDetailDto.releaseYear(),
                    filmDetailDto.rentalDuration(),
                    filmDetailDto.rentalRate(),
                    filmDetailDto.length(),
                    filmDetailDto.replacementCost(),
                    filmDetailDto.rating(),
                    filmDetailDto.specialFeatures(),
                    filmDetailDto.lastUpdate(),
                    filmDetailDto.languageName(),
                    filmDetailDto.categories() != null ? filmDetailDto.categories() : new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        // Pasar la información al modelo
        model.addAttribute("film", filmDetailDto);
        model.addAttribute("categories", categories);
        model.addAttribute("actorNames", actorNames);
        model.addAttribute("currentActors", currentActors);
        model.addAttribute("ratings", ratings);

        return "maintenance-edit";
    }


    @PostMapping("/edit-confirm")
    public String editConfirm(@ModelAttribute FilmDetailDto filmDetailDto, Model model) {
        boolean updated = maintenanceService.updateFilm(filmDetailDto);
        if (updated) {
            return "redirect:/maintenance/start";
        } else {
            model.addAttribute("error", "Error al actualizar la película.");
            return "maintenance-edit";
        }
    }

    @GetMapping("/add")
    public String add(Model model) {

        List<String> validSpecialFeatures = List.of("Trailers", "Commentaries", "Deleted Scenes", "Behind the Scenes");

        model.addAttribute("validSpecialFeatures", validSpecialFeatures);

        // Otras listas que puedes estar usando
        List<String> categories = maintenanceService.findAllCategoryNames();
        List<String> actorNames = maintenanceService.findAllActorNames();
        List<String> languages = maintenanceService.findAllLanguages();
        List<String> ratings = List.of("G", "PG", "PG-13", "R", "NC-17");

        // Crear un FilmDetailDto vacío
        FilmDetailDto filmDetailDto = new FilmDetailDto(
                null,  // filmId
                "",  // title
                "",  // description
                null,  // releaseYear
                null,  // rentalDuration
                null,  // rentalRate
                null,  // length
                null,  // replacementCost
                "",  // rating
                "",  // specialFeatures
                null,  // lastUpdate
                "",  // languageName
                new ArrayList<>(),  // categories
                new ArrayList<>()   // actors
        );

        // Pasar la información al modelo
        model.addAttribute("film", filmDetailDto);
        model.addAttribute("categories", categories);
        model.addAttribute("actorNames", actorNames);
        model.addAttribute("languages", languages);
        model.addAttribute("ratings", ratings);
        model.addAttribute("validSpecialFeatures", validSpecialFeatures);

        return "maintenance-add";
    }


    @PostMapping("/add")
    public String addConfirm(@ModelAttribute FilmDetailDto filmDetailDto, Model model) {
        System.out.println("Datos recibidos del formulario: " + filmDetailDto);

        boolean saved = maintenanceService.addFilm(filmDetailDto);
        if (saved) {
            System.out.println("Película registrada con éxito");
            return "redirect:/maintenance/start";
        } else {
            System.out.println("Error al registrar la película");
            model.addAttribute("error", "Error al registrar la película.");
            return "maintenance-add";
        }
    }


    @PostMapping("/remove/{id}")
    public String removeFilm(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean deleted = maintenanceService.deleteFilm(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Película eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la película.");
        }
        return "redirect:/maintenance/start";
    }
}








