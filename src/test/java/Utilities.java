import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.enums.HttpMethod;
import org.example.model.Post;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;

/**
 * Класс с утилитарными функциями
 */
public final class Utilities {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    public static void getStubMapping(HttpMethod httpMethod, String uri, String reqJson, String respJson, WireMockExtension wireMockExtension) {

        MappingBuilder mappingBuilder = switch (httpMethod) {
            case GET -> WireMock.get(uri);
            case POST -> WireMock.post(uri);
            case PUT -> WireMock.put(uri);
            case PATCH -> WireMock.patch(uri);
            case DELETE -> WireMock.delete(uri);
        };

        if (httpMethod.equals(HttpMethod.GET)) {
            wireMockExtension.stubFor(
                    mappingBuilder.willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody(reqJson)));

        } else if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
            wireMockExtension.stubFor(
                    mappingBuilder.withRequestBody(equalToJson(reqJson))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                            .withBody(respJson)));

        } else if (httpMethod.equals(HttpMethod.DELETE)) {
            wireMockExtension.stubFor(
                    mappingBuilder.willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                                    .withBody(reqJson)));
        }
    }


    public static void executeRequest(HttpMethod method, String uri, Post postToSend, WireMockExtension wireMockExtension, WebTestClient webTestClient) throws JsonProcessingException {
        String requestJson = MAPPER.writeValueAsString(postToSend);

        // Создаём ответ с теми же данными (как эхо)
        String responseJson = MAPPER.writeValueAsString(postToSend);

        Utilities.getStubMapping(method, uri, requestJson, responseJson, wireMockExtension);

        // Выполняем запрос в зависимости от метода
        WebTestClient.RequestBodySpec spec;
        if (HttpMethod.POST.equals(method)) {
            spec = webTestClient.post().uri("api/posts");
        } else if (HttpMethod.PUT.equals(method)) {
            spec = webTestClient.put().uri("api/posts/1");
        } else {
            throw new UnsupportedOperationException("Unsupported method: " + method);
        }

        spec.contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postToSend)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(postToSend.getId())
                .jsonPath("$.title").isEqualTo(postToSend.getTitle());
    }
}
