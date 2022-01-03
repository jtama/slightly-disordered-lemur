package com.onepoint.sdl;


import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.rbac.ClusterRole;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Arrays;

@ApplicationScoped
public class Initializer {

    private KubernetesClient client;
    private Configuration config;
    private Logger logger;

    public Initializer(KubernetesClient client, Configuration config, Logger logger) {
        this.client = client;
        this.config = config;
        this.logger = logger;
    }

    public void onStart(@Observes StartupEvent ev) {
        logger.info("Configuring application");
        Arrays.stream(config.namespaces())
                .forEach(this::configure);
    }

    private void configure(String namespace) {

        ServiceAccount sa = SlightlyDisorderedLemureManifestHelper.serviceAccount(namespace, config);
        logger.infov("Configuring ''{0}'' service account for ''{1}'' namespace.", sa.getMetadata().getName(), namespace);
        client.serviceAccounts().inNamespace(namespace).createOrReplace(sa);

        ClusterRole cr = SlightlyDisorderedLemureManifestHelper.clusterRole(namespace);
        logger.infov("Configuring ''{0}'' cluster role for ''{1}'' namespace.", cr.getMetadata().getName(), namespace);
        client.rbac().clusterRoles().createOrReplace(cr);

        RoleBinding crb = SlightlyDisorderedLemureManifestHelper.roleBinding(cr, sa);
        logger.infov("Configuring ''{0}'' role bindig for ''{1}'' namespace.", crb.getMetadata().getName(), namespace);
        client.rbac().roleBindings().inNamespace(namespace).createOrReplace(crb);

        Deployment deployment = SlightlyDisorderedLemureManifestHelper.deployment(namespace, sa, config);
        logger.infov("Créating deployment for ''{1}'' namespace.", namespace);
        client.apps().deployments().inNamespace(namespace).createOrReplace(deployment);
    }

}
