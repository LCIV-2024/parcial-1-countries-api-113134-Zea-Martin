package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.PostCountryDto;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCountries() throws Exception {
        Country country = new Country("Argentina", 45000000, 2780400, "ARG", "South America", null, null);
        CountryDTO countryDTO = new CountryDTO("ARG", "Argentina");
        List<Country> countries = new ArrayList<>();
        countries.add(country);

        when(countryService.getAllCountries()).thenReturn(countries);
        when(countryService.mapToDTO(any())).thenReturn(countryDTO);

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));

        verify(countryService, times(1)).getAllCountries();
        verify(countryService, times(1)).mapToDTO(any());
    }

    @Test
    void getCountriesByRegion() throws Exception {
        CountryDTO countryDTO = new CountryDTO("ARG", "Argentina");
        List<CountryDTO> countriesDTO = Collections.singletonList(countryDTO);

        when(countryService.getCountryByRegion("South America")).thenReturn(countriesDTO);

        mockMvc.perform(get("/api/countries/South America/continent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));

        verify(countryService, times(1)).getCountryByRegion("South America");
    }

    @Test
    void getCountriesByLanguage() throws Exception {
        CountryDTO countryDTO = new CountryDTO("ARG", "Argentina");
        List<CountryDTO> countriesDTO = Collections.singletonList(countryDTO);

        when(countryService.getCountryByLanguage("Spanish")).thenReturn(countriesDTO);

        mockMvc.perform(get("/api/countries/Spanish/language"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));

        verify(countryService, times(1)).getCountryByLanguage("Spanish");
    }

    @Test
    void getCountryWithMostBorders() throws Exception {
        CountryDTO countryDTO = new CountryDTO("ARG", "Argentina");

        when(countryService.getCountryDtoWithMoreBorders()).thenReturn(countryDTO);

        mockMvc.perform(get("/api/countries/most-borders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("ARG"))
                .andExpect(jsonPath("$.name").value("Argentina"));

        verify(countryService, times(1)).getCountryDtoWithMoreBorders();
    }

    @Test
    void addCountry() throws Exception {
        PostCountryDto postCountryDto = new PostCountryDto();
        postCountryDto.setAmountOfCountryToSave(5);

        CountryDTO countryDTO = new CountryDTO("ARG", "Argentina");
        List<CountryDTO> countriesDTO = Collections.singletonList(countryDTO);

        when(countryService.saveCountryByAmount(any(PostCountryDto.class))).thenReturn(countriesDTO);

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCountryDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));

        verify(countryService, times(1)).saveCountryByAmount(any(PostCountryDto.class));
    }

    @Test
    void addCountry_TooManyCountries() throws Exception {
        PostCountryDto postCountryDto = new PostCountryDto();
        postCountryDto.setAmountOfCountryToSave(15);

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCountryDto)))
                .andExpect(status().isBadRequest());

        verify(countryService, times(0)).saveCountryByAmount(any(PostCountryDto.class));
    }
}
