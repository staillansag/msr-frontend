FROM default-route-openshift-image-registry.apps.caas-int-hp.automation.edf.fr/ea7-integrationdsit-grp-hp/msr-dce-dev:0.0.3

EXPOSE 5555
EXPOSE 5543
EXPOSE 9999

USER sagadmin

ADD --chown=sagadmin . /opt/softwareag/IntegrationServer/packages/dceFrontEnd

RUN chgrp -R 0 /opt/softwareag/IntegrationServer/packages/dceFrontEnd && chmod -R g=u /opt/softwareag/IntegrationServer/packages/dceFrontEnd
