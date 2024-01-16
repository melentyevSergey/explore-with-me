package ru.practicum.main.stat.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.main.stat.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Value("${stats-server.url}")
    private String serverUrl;

    @Value("${main-app.name}")
    private String appMain;

    @Autowired
    public StatsClient(RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory())
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void hit(HttpServletRequest request) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appMain)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        post(serverUrl + "/hit", hitDto);
    }

    public void hits(List<Integer> events, HttpServletRequest request) {
        List<EndpointHitDto> hitsDto = new ArrayList<>();

        for (Integer eventId : events) {
            hitsDto.add(EndpointHitDto.builder()
                    .app(appMain)
                    .uri("/events/" + eventId)
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
        post(serverUrl + "/hits", hitsDto);
    }

    public ResponseEntity<Object> stats(String start,
                                        String end,
                                        String uris,
                                        Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );

        return get(serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}