package com.onepoint.sdl.rk;

import com.onepoint.sdl.r.RandomRequestSpec;
import com.onepoint.sdl.r.RandomRequestStatus;
import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceListBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import org.assertj.core.api.AutoCloseableBDDSoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@QuarkusTest
@WithKubernetesTestServer
class RandomKillControllerTest {

    @Inject
    RandomKillReconcillier controller;

    @Inject
    WorkerClientFactory clientFactory;

    @KubernetesTestServer
    KubernetesServer mockServer;


    @BeforeAll
    public static void beforeAll() {
        WorkerClientFactory mock = mock(WorkerClientFactory.class, RETURNS_DEEP_STUBS);
        QuarkusMock.installMockForType(mock, WorkerClientFactory.class);
    }

    @BeforeEach
    public void before() {
        mockServer.expect().get().withPath("/api/v1/namespaces/unknown")
            .andReturn(200,
                null)
            .always();
        mockServer.expect().get().withPath("/api/v1/namespaces/dummy")
            .andReturn(200,
                new NamespaceListBuilder()
                    .withItems(new NamespaceBuilder().withNewMetadata().withName("dummy").and().build())
                    .build())
            .always();
        mockServer.expect().get().withPath("/api/v1/namespaces/empty")
            .andReturn(200,
                new NamespaceListBuilder()
                    .withItems(new NamespaceBuilder().withNewMetadata().withName("empty").and().build())
                    .build())
            .always();
    }

    @Test
    void it_should_return_error_status_when_namespace_doesnt_exists() {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("unknown", false));
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.reconcile(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateResourceAndStatus()).isTrue();
            softly.then(updateResource.getResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.ERROR);
        }
    }

    @Test
    void it_should_return_done_status_when_no_pod_exist() {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("empty", false));
        ObjectMeta metadata = new ObjectMetaBuilder().withAnnotations(Map.of()).build();
        rkr.setMetadata(metadata);
        given(clientFactory.getWorkerForNamespace(any()).target()).willReturn("");
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.reconcile(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateResourceAndStatus()).isTrue();
            softly.then(updateResource.getResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.DONE);
            softly.then(updateResource.getResource().getStatus().message()).isEqualTo("Nothing to do.");
        }
    }

    @Test
    void it_should_return_done_status_when_pod_has_been_targeted_and_has_been_deleted() {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("empty", false));
        ObjectMeta metadata = new ObjectMetaBuilder().withAnnotations(Map.of("pod-name", "targetted")).build();
        rkr.setMetadata(metadata);
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.reconcile(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateResourceAndStatus()).isTrue();
            softly.then(updateResource.getResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.DONE);
            softly.then(updateResource.getResource().getStatus().message()).isEqualTo("Pod has been taken care of.");
        }
    }

    @ParameterizedTest(name = "with targetOnly ''{0}'' should annotate pod with ''{1}''")
    @DisplayName("createOrUpdateResource ")
    @MethodSource("processTestValues")
    void it_should_return_done_status_with_annotated_when_pod_has_been_targeted(boolean targetOnly, String message) throws Exception {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("dummy", targetOnly));
        ObjectMeta metadata = new ObjectMetaBuilder().withAnnotations(Map.of()).build();
        rkr.setMetadata(metadata);

        given(clientFactory.getWorkerForNamespace(any()).target()).willReturn("pod0");
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.reconcile(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateResourceAndStatus()).isTrue();
            softly.then(updateResource.getResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.PROCESSING);
            softly.then(updateResource.getResource().getStatus().message()).isEqualTo("Slightly disordered lemure target is 'pod0' 🎯.");
            softly.then(updateResource.getResource().getMetadata().getAnnotations().get("pod-name")).isEqualTo("pod0");
            if (!targetOnly) {
                softly.thenCode(() -> verify(clientFactory.getWorkerForNamespace(any())).kill(eq("pod0"))).doesNotThrowAnyException();
            }
        }
    }

    static Stream<Arguments> processTestValues() {
        return Stream.of(Arguments.of(true, "Slightly disordered lemure target is 'pod0' 🎯."),
            Arguments.of(false, "Slightly disordered lemure killed 'pod0' 💀."));
    }


}
