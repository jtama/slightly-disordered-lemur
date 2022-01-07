package com.onepoint.sdl;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.rbac.ClusterRole;
import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.fabric8.kubernetes.api.model.rbac.RoleBindingBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SlightlyDisorderedLemureManifestHelper {


    public static ServiceAccount serviceAccount(String namespace, Configuration configuration) {
        return new ServiceAccountBuilder()
            .withNewMetadata()
            .withName("sld-processor")
            .withNamespace(namespace)
            .withAnnotations(Map.of("sld-creation-date", LocalDateTime.now().toString()))
            .endMetadata()
            .addNewImagePullSecret()
            .withName(configuration.secret().orElse(""))
            .endImagePullSecret().build();
    }

    public static ClusterRole clusterRole(String namespace) {
        return new ClusterRoleBuilder()
            .withNewMetadata()
            .withName("sld-processor-role")
            .withAnnotations(Map.of("sld-creation-date", LocalDateTime.now().toString()))
            .endMetadata()
            .addNewRule()
            .addNewApiGroup("")
            .addNewResource("pods")
            .addNewResource("namespaces")
            .addAllToVerbs(List.of("get", "list", "watch", "delete"))
            .endRule()
            .addNewRule()
            .addNewApiGroup("")
            .addNewResource("pods/exec")
            .addAllToVerbs(List.of("get", "create"))
            .endRule()
            .build();
    }

    public static RoleBinding roleBinding(ClusterRole cr, ServiceAccount sa) {
        return new RoleBindingBuilder()
            .withNewMetadata()
            .withName("sld-processor-role-binding")
            .withNamespace(sa.getMetadata().getNamespace())
            .withAnnotations(Map.of("sld-creation-date", LocalDateTime.now().toString()))
            .endMetadata()
            .addNewSubject()
            .withKind(sa.getKind())
            .withName(sa.getMetadata().getName())
            .withNamespace(sa.getMetadata().getNamespace())
            .endSubject()
            .withNewRoleRef()
            .withKind(cr.getKind())
            .withName(cr.getMetadata().getName())
            .endRoleRef()
            .build();
    }

    public static Deployment deployment(String namespace, ServiceAccount sa, Configuration config) {
        return new DeploymentBuilder()
            .withNewMetadata()
            .withName("sld-worker")
            .withNamespace(namespace)
            .withLabels(Map.of("operator", "sld", "type", "worker"))
            .withAnnotations(Map.of("sld-creation-date", LocalDateTime.now().toString()))
            .endMetadata()
            .withNewSpec()
            .withReplicas(1)
            .withNewSelector()
            .withMatchLabels(Map.of("operator", "sld", "type", "worker"))
            .endSelector()
            .withNewTemplate()
            .withNewMetadata()
            .withLabels(Map.of("operator", "sld", "type", "worker"))
            .endMetadata()
            .withNewSpec()
            .withContainers(containers(namespace, sa, config))
            .withServiceAccountName(sa.getMetadata().getName())
            .addNewImagePullSecret()
            .withName(config.secret().orElse(""))
            .endImagePullSecret()
            .endSpec()
            .endTemplate()
            .endSpec()
            .build();

    }

    public static Service service(String namespace) {
        return new ServiceBuilder()
            .withNewMetadata()
            .withName("sld-worker-svc")
            .withNamespace(namespace)
            .withLabels(Map.of("operator", "sld", "type", "worker"))
            .withAnnotations(Map.of("sld-creation-date", LocalDateTime.now().toString()))
            .endMetadata()
            .withNewSpec()
            .addNewPort()
            .withName("http")
            .withPort(80)
            .withNewTargetPort("http")
            .endPort()
            .addToSelector(Map.of("operator", "sld", "type", "worker"))
            .endSpec()
            .build();
    }

    private static List<Container> containers(String namespace, ServiceAccount sa, Configuration config) {
        return List.of(
            new ContainerBuilder()
                .withName("sld-worker")
                .withImage("%s/slightly-disordered-worker:1.0".formatted(config.privateRegistry()))
                .withImagePullPolicy("Always")
                .withNewLivenessProbe()
                .withFailureThreshold(3)
                .withNewHttpGet()
                .withPath("/q/health/live")
                .withNewPort(8080)
                .withScheme("HTTP")
                .endHttpGet()
                .withInitialDelaySeconds(0)
                .withPeriodSeconds(30)
                .withSuccessThreshold(1)
                .withTimeoutSeconds(10)
                .endLivenessProbe()
                .withNewReadinessProbe()
                .withFailureThreshold(3)
                .withNewHttpGet()
                .withPath("/q/health/ready")
                .withNewPort(8080)
                .withScheme("HTTP")
                .endHttpGet()
                .withInitialDelaySeconds(0)
                .withPeriodSeconds(30)
                .withSuccessThreshold(1)
                .withTimeoutSeconds(10)
                .endReadinessProbe()
                .withPorts(List.of(new ContainerPortBuilder()
                    .withName("http")
                    .withProtocol("TCP")
                    .withContainerPort(8080)
                    .build()
                ))
                .build());
    }
}
