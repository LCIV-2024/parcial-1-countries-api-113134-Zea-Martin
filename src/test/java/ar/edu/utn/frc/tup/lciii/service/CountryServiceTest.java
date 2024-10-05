package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.PostCountryDto;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CountryService countryService;

    private List<Map<String, Object>> mockApiResponse;

    @BeforeEach
    void setUp() {

        // MockitoAnnotations.openMocks(this);

        mockApiResponse = new ArrayList<>();
        Map<String, Object> countryData = new HashMap<>();
        countryData.put("name", Collections.singletonMap("common", "Argentina"));
        countryData.put("population", 45376763);
        countryData.put("area", 2780400);
        countryData.put("cca3", "ARG");
        countryData.put("region", "Americas");
        countryData.put("borders", Arrays.asList("CHL", "BOL", "BRA"));
        countryData.put("languages", Collections.singletonMap("spa", "Spanish"));
        mockApiResponse.add(countryData);

        when(restTemplate.getForObject(any(String.class), eq(List.class))).thenReturn(mockApiResponse);
    }

    @Test
    void getAllCountries() {
        List<Country> countries = countryService.getAllCountries();
        assertEquals(1, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
    }

    @Test
    void mapToCountry() {
        Country country = countryService.mapToCountry(mockApiResponse.get(0));
        assertEquals("Argentina", country.getName());
        assertEquals("ARG", country.getCode());
        assertEquals(45376763, country.getPopulation());
        assertEquals(2780400, country.getArea());
        assertEquals("Americas", country.getRegion());
        assertEquals(Arrays.asList("CHL", "BOL", "BRA"), country.getBorders());
        assertEquals(Collections.singletonMap("spa", "Spanish"), country.getLanguages());
    }

    @Test
    void mapToDTO() {
        Country country = new Country("Argentina", 45195777, 2780400.0, "ARG", "Americas", null, null);
        CountryDTO countryDTO = countryService.mapToDTO(country);
        assertEquals("ARG", countryDTO.getCode());
        assertEquals("Argentina", countryDTO.getName());
    }

    @Test
    void getCountryDTOForName() {
        CountryDTO countryDTO = countryService.getCountryDTOForName("Argentina");
        assertNotNull(countryDTO);
        assertEquals("ARG", countryDTO.getCode());
    }

    @Test
    void getCountryDTOForCode() {
        CountryDTO countryDTO = countryService.getCountryDTOForCode("ARG");
        assertNotNull(countryDTO);
        assertEquals("Argentina", countryDTO.getName());
    }

    @Test
    void getCountryByRegion() {
        List<CountryDTO> countriesDTO = countryService.getCountryByRegion("Americas");
        assertEquals(1, countriesDTO.size());
        assertEquals("ARG", countriesDTO.get(0).getCode());
    }

    @Test
    void getCountryByLanguage() {
        List<CountryDTO> countriesDTO = countryService.getCountryByLanguage("Spanish");
        assertEquals(1, countriesDTO.size());
        assertEquals("ARG", countriesDTO.get(0).getCode());
    }

    @Test
    void getCountryDtoWithMoreBorders() {
        CountryDTO countryDTO = countryService.getCountryDtoWithMoreBorders();
        assertNotNull(countryDTO);
        assertEquals("ARG", countryDTO.getCode());
    }

    @Test
    void saveCountryByAmount() {
        PostCountryDto postCountryDto = new PostCountryDto();
        postCountryDto.setAmountOfCountryToSave(1);

        when(countryRepository.save(any(CountryEntity.class))).thenReturn(new CountryEntity());

        List<CountryDTO> savedCountries = countryService.saveCountryByAmount(postCountryDto);
        assertEquals(1, savedCountries.size());
        assertEquals("ARG", savedCountries.get(0).getCode());

        ArgumentCaptor<CountryEntity> countryEntityCaptor = ArgumentCaptor.forClass(CountryEntity.class);
        verify(countryRepository, times(1)).save(countryEntityCaptor.capture());
    }
}
