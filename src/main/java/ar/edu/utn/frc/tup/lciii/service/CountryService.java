package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.PostCountryDto;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    private final RestTemplate restTemplate;

    private final ModelMapper modelMapper;

    public List<Country> getAllCountries() {
        String url = "https://restcountries.com/v3.1/all";
        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
        return response.stream().map(this::mapToCountry).collect(Collectors.toList());
    }

    /**
     * Agregar mapeo de campo cca3 (String)
     * Agregar mapeo campos borders ((List<String>))
     */
    public Country mapToCountry(Map<String, Object> countryData) {
        Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
        return Country.builder()
                .name((String) nameData.get("common"))
                .population(((Number) countryData.get("population")).longValue())
                .area(((Number) countryData.get("area")).doubleValue())
                .code((String) countryData.get("cca3"))
                .region((String) countryData.get("region"))
                .borders((List<String>) countryData.get("borders"))
                .languages((Map<String, String>) countryData.get("languages"))
                .build();
    }


    public CountryDTO mapToDTO(Country country) {
        return new CountryDTO(country.getCode(), country.getName());
    }

    public CountryDTO getCountryDTOForName(String name) {
        List<Country> countries = getAllCountries();
        for (Country country : countries) {
            if (country.getName().equals(name)) {
                return mapToDTO(country);
            }
        }
        return null;
    }

    public CountryDTO getCountryDTOForCode(String code) {
        List<Country> countries = getAllCountries();
        for (Country country : countries) {
            if (country.getCode().equals(code)) {
                return mapToDTO(country);
            }
        }
        return null;
    }

    public List<CountryDTO> getCountryByRegion(String continent) {
        List<Country> countries = getAllCountries();
        List<CountryDTO> countriesDTO = new ArrayList<>();

        for (Country country : countries) {
            if (country.getRegion().equals(continent)) {
                countriesDTO.add(mapToDTO(country));
            }
        }

        return countriesDTO;
    }

    public List<CountryDTO> getCountryByLanguage(String language) {
        List<Country> countries = getAllCountries();
        List<CountryDTO> countriesDTO = new ArrayList<>();

        for (Country country : countries) {
            if (country.getLanguages() != null) {
                if (country.getLanguages().containsValue(language)) {
                    countriesDTO.add(mapToDTO(country));
                }
            }
        }

        return countriesDTO;
    }

    public CountryDTO getCountryDtoWithMoreBorders() {
        List<Country> countries = getAllCountries();
        Country countryWithMoreBorders = countries.get(0);
        for (Country country : countries) {
            if (country.getBorders() != null) {
                if (country.getBorders().size() > countryWithMoreBorders.getBorders().size()) {
                    countryWithMoreBorders = country;
                }                ;
            }
        }
        return mapToDTO(countryWithMoreBorders);
    }

    public List<CountryDTO> saveCountryByAmount(PostCountryDto postCountryDto) {

        int amount = postCountryDto.getAmountOfCountryToSave();
        //a. Obtener todos los países desde la API: El endpoint debe realizar una solicitud a una API externa que devuelva información sobre todos los países disponibles.

        List<Country> countries = getAllCountries();
        //b. Seleccionar y procesar la cantidad indicada: De la lista de países obtenida, se debe seleccionar la cantidad especificada como parámetro (que no debe ser mayor a 10). Una vez seleccionados, se deben ordenar estos países de manera aleatoria.

        List<Country> selectedCountries = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int randomIndex = (int) (Math.random() * countries.size());
            selectedCountries.add(countries.get(randomIndex));
        }
        Collections.shuffle(selectedCountries);

        //c. Guardar en la base de datos: Luego, se debe guardar en la base de datos los países seleccionados, incluyendo los campos de nombre, código, población (population) y área (Ademas de un id autoincremental).

        for (Country country : selectedCountries) {
            CountryEntity countryEntity = modelMapper.map(country, CountryEntity.class);
            countryRepository.save(countryEntity);
        }

        List<CountryDTO> countryDTOS = new ArrayList<>();
        for (Country country : selectedCountries) {
            countryDTOS.add(mapToDTO(country));
        }
        //d. Devolver los países insertados: Finalmente, se debe devolver el nombre y el código de los países que fueron insertados en la base de datos.
        return countryDTOS;

    }

}