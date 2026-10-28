package com.sistemas.usuarios.infrastructure.output.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sistemas.usuarios.domain.exception.ServicioExternoException;
import com.sistemas.usuarios.domain.spi.IRestauranteExternoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestauranteExternoAdapter implements IRestauranteExternoPort {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RestauranteRespuesta(Long id, Long idPropietario) {
    }

    private final RestTemplate restTemplate;
    private final String urlBasePlazoleta;

    public RestauranteExternoAdapter(RestTemplate restTemplate,
                                     @Value("${app.plazoleta.url}") String urlBasePlazoleta) {
        this.restTemplate = restTemplate;
        this.urlBasePlazoleta = urlBasePlazoleta;
    }

    @Override
    public Long obtenerIdPropietario(Long idRestaurante) {
        try {
            String url = urlBasePlazoleta + "/restaurantes/" + idRestaurante;
            RestauranteRespuesta respuesta = restTemplate.getForObject(url, RestauranteRespuesta.class);
            return respuesta != null ? respuesta.idPropietario() : null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (RestClientException e) {
            throw new ServicioExternoException("No fue posible comunicarse con el microservicio Plazoleta");
        }
    }
}