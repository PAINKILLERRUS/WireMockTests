import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.example.Main;
import org.example.enums.HttpMethod;
import org.example.model.Comment;
import org.example.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.generator.ObjectsGenerator.generatingCommentsAccordingASpecifiedNumber;
import static org.example.generator.ObjectsGenerator.generatingPostsAccordingToASpecifiedNumber;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@WireMockTest()
public class WireMockPostTests {

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().dynamicPort().notifier(new ConsoleNotifier(true)))
            .build();

    @DynamicPropertySource
    public static void setUpMockBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("base_post_url", wireMockExtension::baseUrl);
    }

    @Test
    @SneakyThrows
    @DisplayName("Мок-тест единичного поста")
    public void singlePostMockTest() {
        List<Post> posts = generatingPostsAccordingToASpecifiedNumber(0);
        String jsonPosts = mapper.writeValueAsString(posts.get(0));

        Utilities.getStubMapping(HttpMethod.GET, "/posts/1", jsonPosts, null, wireMockExtension);

        webTestClient.get().uri("api/posts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(posts.get(0).getId())
                .jsonPath("$.body").isEqualTo(posts.get(0).getBody())
                .jsonPath("$.title").isEqualTo(posts.get(0).getTitle())
                .jsonPath("$.userId").isEqualTo(posts.get(0).getUserId());
    }

    @Test
    @SneakyThrows
    @DisplayName("Мок-тест списка постов")
    public void multiPostsMockTest() {
        List<Post> posts = generatingPostsAccordingToASpecifiedNumber(99);
        String jsonPosts = mapper.writeValueAsString(posts);

        Utilities.getStubMapping(HttpMethod.GET, "/posts", jsonPosts, null, wireMockExtension);

        webTestClient.get().uri("api/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class)
                .consumeWith(response -> {
                    List<Post> actualPostsList = response.getResponseBody();
                    assertThat(actualPostsList).hasSize(100);
                    assertThat(actualPostsList).containsAnyElementsOf(posts);
                });
    }

    @Test
    @SneakyThrows
    @DisplayName("Мок-тест списка комментариев")
    public void commentsMockTest() {
        List<Comment> comments = generatingCommentsAccordingASpecifiedNumber(4);
        String jsonComments = mapper.writeValueAsString(comments);

        Utilities.getStubMapping(HttpMethod.GET, "/posts/1/comments", jsonComments, null, wireMockExtension);

        webTestClient.get().uri("api/posts/1/comments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Comment.class)
                .consumeWith(response -> {
                    List<Comment> actualCommentsList = response.getResponseBody();
                    assertThat(actualCommentsList).hasSize(5);
                    assertThat(actualCommentsList).containsAnyElementsOf(comments);
                });
    }

    @Test
    @SneakyThrows
    @DisplayName("Отправка сгенерированного мок-поста методом POST")
    public void sendingTheGeneratedMokPostUsingTheMethodPostTest() {
        List<Post> posts = generatingPostsAccordingToASpecifiedNumber(0);
        Post postToSend = posts.get(0);

        Utilities.executeRequest(HttpMethod.POST, "/posts", postToSend, wireMockExtension, webTestClient);
    }

    @Test
    @SneakyThrows
    @DisplayName("Отправка сгенерированного мок-поста методом PUT")
    public void sendingTheGeneratedMokPostUsingTheMethodPutTest() {
        List<Post> posts = generatingPostsAccordingToASpecifiedNumber(0);
        Post postToSend = posts.get(0);

        Utilities.executeRequest(HttpMethod.PUT, "/posts/1", postToSend, wireMockExtension, webTestClient);
    }

    @Test
    @SneakyThrows
    @DisplayName("Мок тест на удаление поста")
    public void deletingPostMockTest() {
        Utilities.getStubMapping(HttpMethod.DELETE, "/posts/1", "Deleted post 1", null, wireMockExtension);

        webTestClient.delete().uri("api/posts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
    }
}
