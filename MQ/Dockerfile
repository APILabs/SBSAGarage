FROM docker-registry.default.svc:5000/mq/ibm-mqadvanced-server-integration:9.1.3.0-r1-amd64
# Add the admin user as a member of the mqm group and set their password
# Adding new config 
USER root
RUN useradd admin -G mqm \
    && echo admin:passw0rd | chpasswd \
# Create the mqclient group
    && groupadd mqclient \
# Create the app user as a member of the mqclient group and set their password
    && useradd app -G mqclient \
    && echo app:passw0rd | chpasswd
# Copy the configuration script to /etc/mqm where it will be picked up automatically
USER mqm
COPY config.mqsc /etc/mqm/
