package com.onepoint.sdl;

import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceListBuilder;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@WithKubernetesTestServer
class KillControllerTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @BeforeEach
    public void before() {
        mockServer.expect().get().withPath("/api/v1/namespaces/dummy")
            .andReturn(200,
                new NamespaceListBuilder()
                    .withItems(new NamespaceBuilder().withNewMetadata().withName("dummy").and().build())
                    .build())
            .always();

    }

    @Test
    public void it_should_target_random_pod() {
        // Given
        mockServer.expect().get().withPath("/api/v1/namespaces/dummy/pods?fieldSelector=metadate.name%21%3Dsld-worker")
            .andReturn(200,
                new PodListBuilder()
                    .withItems(new PodBuilder()
                        .withNewMetadata()
                        .withName("charly")
                        .withNamespace("dummy")
                        .and().build())
                    .build())
            .once();
        //When, Then
        given()
            .when().get("/pods")
            .then()
            .statusCode(200)
            .body(is("charly"));
    }

    @Test
    public void it_should_return_empty_string() {
        // Given
        // When, Then
        given()
            .when().get("/pods")
            .then()
            .statusCode(200)
            .body(is(""));
    }

    @Test
    public void it_should_return_no_content_on_no_killing() {
        // Given
        // When, Then
        given()
            .when().post("/pods/dummy/kill")
            .then()
            .statusCode(RestResponse.StatusCode.NO_CONTENT);
    }


    @Test
    public void it_should_return_accepted_on_killing() {
        // Given
        mockServer.expect().delete().withPath("/api/v1/namespaces/dummy/pods/charly")
            .andReturn(200,
                new PodBuilder()
                    .withNewMetadata()
                    .withName("charly")
                    .withNamespace("dummy")
                    .and().build())
            .once();
        // When, Then
        given()
            .when().post("/pods/charly/kill")
            .then()
            .statusCode(RestResponse.StatusCode.ACCEPTED);
    }

    @Test
    public void it_should_return_no_content_on_no_invasion() {
        // Given
        // When, Then
        given()
            .when().post("/pods/dummy/invade")
            .then()
            .statusCode(RestResponse.StatusCode.NO_CONTENT);
    }


    @Test
    public void it_should_return_accepted_on_invasion() {
        // Given
        mockServer.expect().get().withPath("/api/v1/namespaces/dummy/pods/charly")
            .andReturn(200,
                new PodListBuilder()
                    .withItems(new PodBuilder()
                        .withNewMetadata()
                        .withName("charly")
                        .withNamespace("dummy")
                        .and().build())
                    .build())
            .once();
        mockServer.expect().get().withPath("/api/v1/namespaces/dummy/pods/charly/exec?command=sh&command=-c&command=echo%20%5C%5C%22%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%E2%80%9E--%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%28%5C%27%3A%3A%3A%3A%5C%27%5C%27%29%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%5C%27%7C%5C%27%5C%27%7C%5C%27%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%7C%3A%3A%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%7C%3A%3A%3A%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%7C%3A%3A%3A%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%7C%3A%3A%3A%5C%27%7C%E2%80%9E_%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%E2%80%9E-*%C2%AF**%C2%AF%3A%3A%3A*-%E2%80%9E%E2%80%9E%E2%80%9E_%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%E2%80%9E%E2%80%9E-%5E*%C2%AF*%5E%7E%7E%7E%5E*%C2%AF%20%3A%20%3A%20%3A%C2%AF*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-%5E*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%2F%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%27%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%27%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%E2%80%9E%E2%80%9E-%7E--------%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E---%7E%5E**%C2%AF*%5E-%E2%80%9E%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E____%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%C2%AF*-%E2%80%9E%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%2F%20%3A%20%3A%20%3A%20%3A%E2%80%9E-%5E*%C2%AF**%5E%7E-%E2%80%9E%E2%80%9E%E2%80%9E%3B%3B%3B%3B%3B%3B%E2%80%9E-%5E*%C2%AF%20*%5E%E2%80%9E%3B%C2%AF*-%E2%80%9E*%2C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%2F%20.%20.__.%20.%20.%20.%20.%C2%AF*%E2%80%9E*%20__%20.%20.%20.%20%5C%3B%3B%3B%3B%5C%27%5C%3A%5C%27%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%7C%20.%20.%7C%7C%7C%7C%7C.%20.%20.%20.%20.%20.%5C%27%7C%20.%20%7C%7C%7C%7C%7C%20.%20.%20.%2C%2F%3B%3B%3B%3B%3B%7C%20%5C%27%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A*-%E2%80%9E%20%3A%20%3A%20*%7E-%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%2F%5C%27___%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E-*_%E2%80%9E%E2%80%9E%E2%80%9E-*%E2%80%9E%2F%5C%27%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20*%5E%7E%7E------------------------%E2%80%9E-----%7E%5E%5E*%C2%AF%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3A%20%3A%20%3A%20%3A%20%3A%E2%80%9E%E2%80%9E--%2C%5E%5E****%2C***%5E%5E%2C%7E%7E%7E%2C%7E%7E*%5C%27%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3A%20%3A%20%3A%20%3A%2F**%5E%7E%7C-----%E2%80%9E%E2%80%9E%7C%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E-%7C%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E-%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3A%20%3A%20%3A%20%5C%27%5C%E2%80%9E_%20.%20%7C%20.%20.%20.%20%5C%27%7C%20.%20.%20.%20%5C%27%7C%20.%20.%20.%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%5C%27%7C%20%3A%20%3A%20%3A%20%3A*-%E2%80%9E%C2%AF%5C%27%7C*%5E%5E%5E%5E*%7C******%7C**%5C%27%5C%27%C2%AF%5C%27%7C*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%E2%80%9E-*-%E2%80%9E%20%3A%20%3A%20%3A%20%3A%20%3A%C2%AF%C2%AF***********%5C%27%5C%27%5C%27%5C%27%C2%AF%C2%AF%C2%AF%20%3A%20%3A%7C-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%9E-%5E*%20%3A%20%3A%20%3A%20%C2%AF**%5E%5E%7E-%E2%80%9E%E2%80%9E%E2%80%9E___%20%3A%20%3A%20%3A__%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E-%7E%5E%5E*%20%3A%20%3A*%7E%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%C2%AF%C2%AF%C2%AF%C2%AF%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20*%5E-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C*%5E%7E-%E2%80%9E%E2%80%9E%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20_%E2%80%9E-%5E*%5C%27%20%5C%27%7C-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%9E-%5E*%C2%AF**%5E%7E%E2%80%9E%C2%AF**%5E%5E%7E---%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E___%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20___%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E--%7E%5E%5E*%C2%AF%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%5C%27%5C%27%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A*-%E2%80%9E%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%C2%AF%C2%AF%C2%AF%5C%27%5C%27%5C%27%5C%27%5C%27%5C%27%5C%27%5C%27%5C%27%5C%27%C2%AF%C2%AF%C2%AF%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%7C%20%3A%20%3A%5C%27%5C%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%20%E2%80%9E%E2%80%9E-%7E%5E**-%E2%80%9E%20%3A%20%3A%20%3A%20%3A%5C%27%5C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20_%E2%80%9E%E2%80%9E%E2%80%9E-%E2%80%9E%20%3A%20%3A%20%3A%7C%20%3A%20%3A%20%7C%20%5C%27%5C%27*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%9E-*%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%5C%20%3A%20%3A%20%3A%20%3A%7C%20%3A%20%3A%20%E2%80%9E-%7E%5E%7E%7E%7E-------%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E-%7E%5E%5E***%C2%AF%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%5C%27%7C%20%3A%20%3A%20%2F%20%3B%20%E2%80%9E-*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%9E-*%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%E2%80%9E-*%20%3A%20%3A%20%3A%20%2F%20%3A%20%3A%20%3A%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%5C%27%7C%20%3A%20%E2%80%9E%2F%E2%80%9E-%5E*%20%3B%20%3B%20%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6..%2F%C2%AF%C2%AF*%5E-%E2%80%9E%E2%80%9E%20%3B%20%3B%E2%80%9E-*%20%3A%20%3A%20%E2%80%9E%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%5C%27%7C-*-%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%27%5C%0A%E2%80%A6%E2%80%A6..%2C%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20*-%2F%20%C2%AF***%C2%AF%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%5C%27%7C%20%3A%20%3A%5C%27%7C%E2%80%A6.*-%E2%80%9E%20%3B%20%3B%E2%80%9E%E2%80%9E-*%20%5C%27%5C%0A%E2%80%A6%E2%80%A6%2C%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%2C%2F%5C%27%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%27%7C%20%3A%20%3A%5C%27%7C%E2%80%A6%E2%80%A6.%5C%C2%AF%20%3B%20%3B%20%3B%20%3B%5C%27%5C%0A%E2%80%A6..%2F***%5E%7E-%E2%80%9E%E2%80%9E%20%3B%2C%2F%E2%80%A6%E2%80%A6%E2%80%A6.%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%5C%27%7C%E2%80%A6%E2%80%A6..%5C%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%5C%0A%E2%80%A6%2C%2F%20%3B%20%3B%20%3B%20%3B%20%3B%C2%AF%5C%27%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%E2%80%9E%5E**%5E%E2%80%9E%20%5C%27%7C%20%3A%20%3A%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6%5C%E2%80%9E%E2%80%9E%E2%80%9E-%7E%5E*%C2%AF%5C%27%5C%0A..%2C%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A*-%E2%80%9E-*%20%3A%7C%20%3A%20%3A%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6.%5C%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%0A..%7C_%E2%80%9E%E2%80%9E%E2%80%9E---%E2%80%9E%E2%80%9E_%5C%27%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%7C%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%7C%0A..%7C%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%5C%27%7C%20%3A%20%3A%20%7C%E2%80%A6%E2%80%A6%E2%80%A6..%7C%7E-%E2%80%9E%E2%80%9E--%5E*%5C%27%5C%27%7C%0A..%7C%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%7C%20%3A%20%3A%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6..%5C%27%7C%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%7C%0A..%5C%27%5C%20%3B%20%3B%20%3B%20_%E2%80%9E%E2%80%9E%E2%80%9E%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%5C%27%7C%20%3A%20%3A%20%7C%E2%80%A6%E2%80%A6%E2%80%A6...%7C%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%7C%0A%E2%80%A6%5C%27%5C%E2%80%9E-%5E*%C2%AF%20%3B%20%3B%20%5C%27%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%7C%20%3A%20%3A%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%7C%5E%7E%7E%7E%5E*%5C%27%5C%27%7C%0A%E2%80%A6.%5C%27%5C%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%E2%80%9E-*%20%3A%20%3A%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%7C%20%3B%20%3B%20%3B%20%3B%20%3B%20%7C%0A%E2%80%A6...%5C%27%5C%20%3B%20%3B%20%3B%20%3B%E2%80%9E%E2%80%9E-%5E*-%E2%80%9E%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A*%7E-%E2%80%9E%E2%80%9E_%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20__%E2%80%9E%E2%80%9E%E2%80%9E--%5E*%5C%27%5C%27%20%3A%20%3A%20%3A%20%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%5C%27%7C%20%3B%20%3B%20%3B%20%3B_%E2%80%9E%7C%0A%E2%80%A6%E2%80%A6..*-%E2%80%9E%E2%80%9E-*%20%3B%20%3B%20%3B%20%3B%5C%27*-%E2%80%9E%E2%80%A6%E2%80%A6%E2%80%A6..%5C%27%7C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%C2%AF%C2%AF******%C2%AF%C2%AF%C2%AF%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A_%E2%80%9E-*%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%2F%C2%AF*****%C2%AF%20%3A%20%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6.*-%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%E2%80%9E-%5E**%5E--%E2%80%9E%E2%80%A6%E2%80%A6%5C%27*%7E-%E2%80%9E%E2%80%9E_%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20__%E2%80%9E%E2%80%9E-%5E*%E2%80%9E%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%2F%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.*-%E2%80%9E%20%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%C2%AF*%5E-%E2%80%9E%E2%80%A6..%5C%20%C2%AF*****%5E%5E%7E%7E%E2%80%9E%7E----%7E%7E%5E%5E-%E2%80%9E**%C2%AF%20%3B%20%3B%20%3B%20%3B%20%3B*-%E2%80%9E%E2%80%A6%E2%80%A6%E2%80%A6..%2F%E2%80%9E_%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%27%5C%27%5C%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%E2%80%9E-%5E**%5C%E2%80%A6.*-%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.*-%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%E2%80%9E-%5C%27-%E2%80%9E%E2%80%A6%E2%80%A6.%2F%20%3A%20%C2%AF%2C*%5E%5E%5E%5E%5E%5E%2C**%C2%AF%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%20%3A%20%3A%20%3A%20%3A%20%E2%80%9E-%5E*%5C%27%20%3A%20%5C%20%3A%20%3A%5C%E2%80%A6...%5C___%E2%80%9E%E2%80%9E%E2%80%9E-%5E%5E*%5C%27%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%5C%27*-%E2%80%9E%20%3B%20_%E2%80%9E-%5E*%20%3B%20%3B%20%5C%E2%80%A6.%2F%E2%80%9E_%E2%80%9E%2C-*.%2F%20%3A%20%3A%20%2F..%5C%27%7C%20%3A%20%3A%20%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%5C_%E2%80%9E-%5E*%20%3A%20%5C%20%3A%20%3A%20%3A%5C%E2%80%9E%E2%80%9E-*%E2%80%A6%E2%80%A6%5C%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%C2%AF%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%E2%80%A6%E2%80%A6%E2%80%A6.*%7E--*%E2%80%A6%2C***%C2%AF%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%2F%20%5C%20%3A%20%3A%20%3A%5C%27%7C_%E2%80%9E%E2%80%9E-*%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%5C%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.*-%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B%20%E2%80%9E-%5C%27%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%C2%AF*%7E%7E*%5C%27%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%5C%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%E2%80%9E%5C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%5C%20%3B%20_%E2%80%9E%E2%80%9E-%5E*%20%3B%20%5C%27%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%5E%5E%5E%5E%5E%5E**%C2%AF%20%3B%20%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%5C%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%27%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%5C%27%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%7C%20%3B%20%3B%20%3B%20%3B%20%3B%20%E2%80%9E%E2%80%9E%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%7C%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%5C%27%7C%5E%5E%5E%5E%5E%5E**%C2%AF%20%5C%27%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%2F*%5E%7E--%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E-%5E*%5C%27%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%7C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%5C%27%2F%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%2F*%5E%7E------%5E*%2F%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%C2%AF*%5E%7E--%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E--%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%2F%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-*%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%2F%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%E2%80%9E-*-%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%2F%20%3B%20%C2%AF*%5E%5E%5E%5E%5E*%5C%27%2F%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-*%20%3B%20%3B%20%3B%C2%AF*%5E%7E-%E2%80%9E%E2%80%9E%E2%80%9E%5C%27%2F%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%E2%80%9E-%5E*%2F%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B_%E2%80%9E%2F%5E-%E2%80%9E%E2%80%9E_%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6_%E2%80%9E%E2%80%9E-*%E2%80%9E%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%20%3B%E2%80%9E-*%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-%5E*%20%3A%20%3A%20%3A%C2%AF**%5E%5E%5E%5E%5E*%20%3A%20%3A%20%3A%20%3A%20%3A*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%E2%80%9E-%5E*%20%3A%20%3A%20%3A%20*%7E--%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E---%5E*-%E2%80%9E%E2%80%9E%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A*-%E2%80%9E%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%E2%80%9E-*%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20*%5E-%E2%80%9E.......%2F%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6...%2F%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20*-%E2%80%9E...%2F_%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A_%E2%80%9E-*%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6.%2F%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%5C%E2%80%A6%C2%AF%C2%AF**%5E%7E%7E%7E---%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E---%5E%5E%5E***%C2%AF%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%C2%AF*%5E%7E---%E2%80%9E%E2%80%9E_%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20%3A%20_%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E%E2%80%9E----%5E*%5C%27%0A%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6%E2%80%A6..%C2%AF%C2%AF%C2%AF%C2%AF*********%C2%AF%0ADisordered%20Lemure%0A%5C%5C%22%20%3E%20%2Ftmp%2Finvasion.txt&stdout=true")
            .andUpgradeToWebSocket()
            .open()
            .done()
            .once();
        // When, Then
        given()
            .when().post("/pods/charly/invade")
            .then()
            .statusCode(RestResponse.StatusCode.ACCEPTED);
    }

}
