package com.onepoint.yap.rk;

import com.onepoint.yap.r.RandomRequestSpec;
import com.onepoint.yap.r.RandomRequestStatus;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceListBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.javaoperatorsdk.operator.api.UpdateControl;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import org.assertj.core.api.AutoCloseableBDDSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

@QuarkusTest
@WithKubernetesTestServer
class RandomKillControllerTest {

    @Inject
    RandomKillController controller;

    @KubernetesTestServer
    KubernetesServer mockServer;

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
        UpdateControl<RandomKillRequest> updateResource = controller.createOrUpdateResource(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateCustomResource()).isFalse();
            softly.then(updateResource.isUpdateStatusSubResource()).isTrue();
            softly.then(updateResource.getCustomResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.ERROR);
        }
    }

    @Test
    void it_should_return_done_status_when_no_pod_exist() {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("empty", false));
        ObjectMeta metadata= new ObjectMetaBuilder().withAnnotations(Map.of()).build();
        rkr.setMetadata(metadata);
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.createOrUpdateResource(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateCustomResourceAndStatusSubResource()).isTrue();
            softly.then(updateResource.getCustomResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.DONE);
            softly.then(updateResource.getCustomResource().getStatus().message()).isEqualTo("Nothing to do.");
        }
    }

    @Test
    void it_should_return_done_status_when_pod_has_been_targeted_and_has_been_deleted() {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("empty", false));
        ObjectMeta metadata= new ObjectMetaBuilder().withAnnotations(Map.of("pod-name", "targetted")).build();
        rkr.setMetadata(metadata);
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.createOrUpdateResource(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateCustomResourceAndStatusSubResource()).isTrue();
            softly.then(updateResource.getCustomResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.DONE);
            softly.then(updateResource.getCustomResource().getStatus().message()).isEqualTo("Pod has been taken care of.");
        }
    }

    @Test
    void it_should_return_done_status_with_annotated_when_pod_has_been_targeted_and_has_been_deleted() {
        //given
        RandomKillRequest rkr = new RandomKillRequest();
        rkr.setSpec(new RandomRequestSpec("dummy", true));
        ObjectMeta metadata= new ObjectMetaBuilder().withAnnotations(Map.of()).build();
        rkr.setMetadata(metadata);
        mockServer.expect().get().withPath("/api/v1/namespaces/dummy/pods")
                .andReturn(200,
                        new PodListBuilder()
                                .withItems(new PodBuilder().withNewMetadata().withName("To be killed").and().build())
                                .build())
                .always();
        // When
        UpdateControl<RandomKillRequest> updateResource = controller.createOrUpdateResource(rkr, null);
        // Then
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(updateResource.isUpdateCustomResourceAndStatusSubResource()).isTrue();
            softly.then(updateResource.getCustomResource().getStatus().state()).isEqualTo(RandomRequestStatus.State.DONE);
            softly.then(updateResource.getCustomResource().getStatus().message()).isEqualTo("Slightly disordered lemure target is 'To be killed' 🎯");
            softly.then(updateResource.getCustomResource().getMetadata().getAnnotations().get("pod-name")).isEqualTo("To be killed");
        }
    }


}
