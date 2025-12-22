package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class StatsClientImpl implements StatsClient {

    private final RestClient restClient;

    public StatsClientImpl(@Value("${stats-server.url}") String serverUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    @Override
    public void hit(EndpointHitDto hit) {
        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hit)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public List<ViewStatsDto> getStats(String start,
                                       String end,
                                       List<String> uris,
                                       boolean unique
    ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", encode(start))
                .queryParam("end", encode(end))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> uriBuilder.queryParam("uris", uri));
        }

        ViewStatsDto[] response = restClient.get()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .body(ViewStatsDto[].class);

        return response == null
                ? List.of()
                : Arrays.asList(response);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
