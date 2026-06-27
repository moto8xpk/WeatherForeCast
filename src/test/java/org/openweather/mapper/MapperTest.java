package org.openweather.mapper;

import org.junit.jupiter.api.Test;
import org.openweather.domain.*;
import org.openweather.model.OpenWeatherMapResponseEntity;
import org.openweather.model.WeatherDomainEntity;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void openWeatherMapResponseMapper_mapsNestedFields() {
        OpenWeatherMapResponse r = new OpenWeatherMapResponse();
        CoordDomain coord = new CoordDomain();
        coord.setLon(106.0);
        coord.setLat(10.0);
        r.coord = coord;
        r.main = MainDomain.builder().temp(30.0).feelsLike(33.0).tempMin(28.0)
                .tempMax(34.0).pressure(1010).humidity(70).seaLevel(1010).grndLevel(1005).build();
        r.wind = WindDomain.builder().speed(4.0).deg(180).build();
        r.clouds = new CloudsDomain(40);
        r.sys = SysDomain.builder().type(1).id(2).country("VN").sunrise(100L).sunset(200L).build();
        r.timezone = 25200;
        r.id = 1566083;
        r.name = "Ho Chi Minh";
        r.cod = 200;
        r.visibility = 10000;
        r.dt = 1_700_000_000L;

        OpenWeatherMapResponseEntity e = OpenWeatherMapResponseMapper.INSTANCE.toEntity(r);

        assertNotNull(e);
        assertEquals(106.0, e.getLon());
        assertEquals(10.0, e.getLat());
        assertEquals(30.0, e.getMainTemp());
        assertEquals(33.0, e.getMainFeelsLike());
        assertEquals(40L, e.getCloudsAll());
        assertEquals("VN", e.getSysCountry());
        assertEquals(1566083L, e.getCityId());
        assertEquals("Ho Chi Minh", e.getName());
    }

    @Test
    void openWeatherMapResponseMapper_handlesNullSource() {
        assertNull(OpenWeatherMapResponseMapper.INSTANCE.toEntity(null));
    }

    @Test
    void openWeatherMapResponseMapper_handlesEmptyResponse() {
        OpenWeatherMapResponseEntity e =
                OpenWeatherMapResponseMapper.INSTANCE.toEntity(new OpenWeatherMapResponse());
        assertNotNull(e);
        assertNull(e.getMainTemp());
        assertNull(e.getSysCountry());
    }

    @Test
    void weatherDomainMapper_mapsFields() {
        WeatherDomain wd = WeatherDomain.builder()
                .id(800L).main("Clear").description("clear sky").icon("01d").build();

        WeatherDomainEntity e = WeatherDomainMapper.INSTANCE.toEntity(wd);

        assertNotNull(e);
        assertEquals(800L, e.getWeatherId());
        assertEquals("Clear", e.getMain());
        assertEquals("clear sky", e.getDescription());
        assertEquals("01d", e.getIcon());
    }

    @Test
    void weatherDomainMapper_handlesNull() {
        assertNull(WeatherDomainMapper.INSTANCE.toEntity(null));
    }
}
