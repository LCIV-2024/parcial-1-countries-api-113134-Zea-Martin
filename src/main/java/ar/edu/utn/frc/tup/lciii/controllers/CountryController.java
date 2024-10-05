package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.PostCountryDto;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> getCountries(@RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String code) {
        if(name == null && code == null) {
            List<Country> countries = countryService.getAllCountries();
            List<CountryDTO> countriesDTO = new ArrayList<>();
            for (Country country : countries) {
                CountryDTO countryDTO = countryService.mapToDTO(country);
                countriesDTO.add(countryDTO);
            }
            return ResponseEntity.ok(countriesDTO);
        }

        if (name != null) {
            CountryDTO countryDTO = countryService.getCountryDTOForName(name);
            if (countryDTO != null) {
                return ResponseEntity.ok(List.of(countryDTO));
            }
            return ResponseEntity.notFound().build();
        }

        code = code.toUpperCase();
        CountryDTO countryDTO = countryService.getCountryDTOForCode(code);
        if (countryDTO != null) {
            return ResponseEntity.ok(List.of(countryDTO));
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/countries/{continent}/continent")
    public ResponseEntity<List<CountryDTO>> getCountriesByRegion(@PathVariable String continent) {
        List<CountryDTO> countriesDTO = countryService.getCountryByRegion(continent);
        return ResponseEntity.ok(countriesDTO);
    }

    @GetMapping("/countries/{language}/language")
    public ResponseEntity<List<CountryDTO>> getCountriesByLanguage(@PathVariable String language) {
        List<CountryDTO> countriesDTO = countryService.getCountryByLanguage(language);
        return ResponseEntity.ok(countriesDTO);
    }

    @GetMapping("/countries/most-borders")
    public ResponseEntity<CountryDTO> getCountryWithMostBorders() {
        CountryDTO countryDTO = countryService.getCountryDtoWithMoreBorders();
        return ResponseEntity.ok(countryDTO);
    }

    @PostMapping("/countries")
    public ResponseEntity<List<CountryDTO>> addCountry(@RequestBody PostCountryDto postCountryDto) {
        if (postCountryDto.getAmountOfCountryToSave() > 10) {
            return ResponseEntity.badRequest().build();
        }
        List<CountryDTO> countriesDTO = countryService.saveCountryByAmount(postCountryDto);
        return ResponseEntity.ok(countriesDTO);
    }

}