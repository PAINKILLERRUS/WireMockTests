import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.enums.HttpMethod;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

/**
 * Класс с утилитарными функциями
 */
public final class Utilities {

    public static void getStubMapping(HttpMethod httpMethod, String uri, String json, WireMockExtension wireMockExtension) {

        MappingBuilder mappingBuilder = switch (httpMethod) {
            case GET -> WireMock.get(uri);
            case POST -> WireMock.post(uri);
            case PUT -> WireMock.put(uri);
            case PATCH -> WireMock.patch(uri);
            case DELETE -> WireMock.delete(uri);
        };

        wireMockExtension.stubFor(
                mappingBuilder.willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(json)));
    }
}
