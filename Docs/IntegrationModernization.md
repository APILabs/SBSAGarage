## Integration Modernization 

The below steps will guide you through the process of starting the app connect enterprise journey from the local system , then into the container and then finally into an open shift Kubernetes cluster. 

## Local System :
## Pre-Requisites:
1.	App connect enterprise toolkit

## Objective: Get started with app connect integration on the local environment.  Connect to the app connect Integration server via the toolkit , enable debug , deploy and test an application. 

## Steps:
1.	Download the ace 11.0.0.x binary based on the platform and install it.
For Windows – chose your preferred directory to install it, else choose the standard folder - C:\Program Files\IBM\ACE\11.0.0.x
For Linux – Untar the download in any preferred location.
2.	Create a workdirectory : You need to create a work directory for ace. This will create the necessary folder structure and the serverconf.yaml file that consists of all the default settings.
3.	Deep dive on the serverconf.yaml properties in the file: 

```
a.	RestAdminListener:
 port: 7610    This is the port to connect to in the toolkit and for the integration server webui.
b.	ResourceManagers	
  JVM:
   jvmDebugPort : 9998  This it to enable the debug port. If this is not configured initially, you will have to stop the integration server to configure it.    
c.	admin Security  This is to enable security for the webui
  Authentication
  basicAuth: true  
  adminSecurity: 'active/inactive’  
  authMode: 'file'
d.	HTTPConnector: 
    #ListenerPort: 0      Default is 7800. Configure this value to change the default port. 
    #CORSEnabled: false  Set this to true to enable REST and cors support.
e.	  HTTPSConnector:
    #ListenerPort: 0   Default is 7843. Configure this value to change the default port. 

    #CORSEnabled: false Set this to true to enable REST and cors support.
```
4.	Start the integration server :
Before you start the integration server, you need to set the profile

```
Commands : 
a.	Set profile   . ./mqsiprofile
b.	Start the server IntegrationServer --name ISIPL101 --work-dir c:\mywrk\myaceworkdir
c.	Connect to the toolkit  localhost:7600

```
Reference Link : https://developer.ibm.com/integration/docs/app-connect-enterprise/get-started/
	
##

## Docker Environment : default image

## Pre-Requisites:
1.	Docker installation
2.	Docker hub login details
3.	ACE binary - ace-11.0.0.5.tar.gz

## Objective: Configure an App Connect Enterprise integration server on the docker environment. Build your own image of app connect enterprise. Connect to the integration server via the toolkit. 

## Steps:

1)	Clone or download the below repository :  https://github.com/ot4i/ace-docker.git
2)	Download a copy of App Connect Enterprise (ie. ace-11.0.0.5.tar.gz) and place it in the deps folder.
3)	Folder structure
	Ace-docker-master 
	 Deps  ace-11.0.0.5.tar.gz
	 ubi  dockerfile.acemq ( docker file to build ace + mq server)
		     dockerfile.aceonly ( docker file to build ace)
		     dockerfile.mqclient ( docker file to build ace + mq client)

4)	Build the docker image with the below commands :

## Standalone Ace integration server : 
```
Command : folder  ace-docker-master: 
docker build -t ace-only --build-arg ACE_INSTALL=IBM_ACE_11.0.0.4_LNX_X8664_INCTKT.tar.gz --file ubi/Dockerfile.aceonly .
```

This will create a docker image with image name “ace-only” with tag “latest”. Confirm by running “docker images” to view the newly built image.
WORKDIR /opt/ibm/ace-11/server/bin
RUN . ./mqsiprofile 
RUN echo $PWD
RUN ./mqsisetdbparms -w /home/aceuser/ace-server -n SAMPLE -u db2inst1 -p passw0rd
5)	Run the docker image to start an ace integration server container.
```
Command : docker run --name aceserver -p 7600:7600 -p 7800:7800 -p 7843:7843 --env LICENSE=accept --env ACE_SERVER_NAME=ACESERVER ace-only:latest
```
6)	Confirm that the integration server container is running – “docker ps”.  If you don’t see any entry, run “docker ps -a” to see if the container started but crashed and didn’t start. If it is running , try connecting to the running integration server using the webui – http://localhost:7600 or using the toolkit.

7)	To view the details of a running container :  Exec into the container and view the existing configuration. 
“docker exec -it <container_id>  /bin/bash”
Navigate to path /home/aceuser/aceserver to view the running configurations of the integration server.


## Docker Environment : custom configuration

## Pre-Requisites:
1.	Docker installation
2.	Download/clone git repository
3.	ACE binary - ace-11.0.0.5.tar.gz

## Objective: Usage of mounts in docker and then using .ounts to inject custom configuration into the ace integration server container. 

## Steps:

1.	Clone or download the below repository :  https://github.com/ot4i/ace-docker.git
2.	Pull the public ibmcom/ace image from docker hub  docker pull ibcom/ace or pre-build the ace image by following the steps of the previous section.
3.	Navigate to the folder : ace-docker-mastersample  initial-config. This folder has sub folders of different configurations. Example : webusers, serverconf. 
a.	For each sub folder that is there in the “initial-config” folder, a script corresponding to that directory is run at start-up of the container. Only keep those folders where you want to add custom configuration.
b.	Note:  Do not keep empty folders. This will cause the scripts to fail at start-up and the integration server will not start.

serverconf  	ace_config_serverconf.sh
webusers	ace_config_webusers.sh
	Example Config : 
	
	 

4.	Once you have updated the files with custom configurations run the below command to start the integration server by using mounts to inject custom configuration into the ace container. Mount the initial config folder into the container on start up by running the below command.

docker run --name aceapp -p 7600:7600 -p 7800:7800 -p 7843:7843 --env LICENSE=accept --env ACE_SERVER_NAME=ACESERVER --mount type=bind,src=/{path to repo}/sample/initial-config,dst=/home/aceuser/initial-config --env ACE_TRUSTSTORE_PASSWORD=truststorepwd --env ACE_KEYSTORE_PASSWORD=keystorepwd aceapp:latest

5.	To make any changes after running, first change the config on the local system and then stop and restart the container for the changes to get updated.

## Docker Environment:  Custom Build with application 

## Pre-Requisites:
1.	Docker installation
2.	Download/clone git repository
3.	Base image of app connect enterprise from docker hub or pre-built image.
4.	Bar file of application. Start with a simple application that does not have dependeny with MQ as this requires an MQ policy to deploy successfully.
5.	Set up the initial-config directory based on the previous steps.


## Objective: Use a custom docker file to build an app connect enterprise image with application bar file and configuration to create an immutable copy.

## Steps:

1.	Clone or download the below repository :  https://github.com/ot4i/ace-docker.git
2.	Pull the public ibmcom/ace image from docker hub  docker pull ibcom/ace or pre-build the ace image by following the steps of the previous section.
3.	Navigate to the folder  ace-docker-master sample. 
4.	Place the application bar files in the folder “bars_aceonly”

5.	View the dockerfile under the sample folder – dockerfile.aceonly


 

6.	Build a custom integration server image with the application bar file by running the below command 
docker build -t aceapp --file Dockerfile.aceonly .
This command creates a new image with the image name – “aceapp” with the tag “latest”. Confirm the image created by running “docker images”
7.	If the initial-config directory config is not updated before, follow the steps mentioned in the previous section.
8.	Run the image to create an integration server with the pre-build application and custom configuration by running the below command 
docker run --name aceapp -p 7600:7600 -p 7800:7800 -p 7843:7843 --env LICENSE=accept --env ACE_SERVER_NAME=ACESERVER --mount type=bind,src=/{path to repo}/sample/initial-config,dst=/home/aceuser/initial-config --env ACE_TRUSTSTORE_PASSWORD=truststorepwd --env ACE_KEYSTORE_PASSWORD=keystorepwd aceapp:latest

9.	Confirm that the application is running –“docker ps”

## Docker Environment:  Configure DB2 

 ## Pre-Requisites:
1.	Docker installation
2.	Download/clone git repository
3.	ODBC cli drivers and libraries


## Objective: To set up a IBM Db2 database on a container and connect to the database

## Steps:
1.	Pull the docker image for db2. 
a.	Docker pull ibmcom/db2

2.	Once the image is successfully pulled run the below command to “run” the db2 image.
```
docker run -itd --name mydb2 --privileged=true -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=passw0rd -e DBNAME=testdb -v /Users/anjanaananth/Documents/ACE_MQ/DB2:/database ibmcom/db2
```
3.	Check if the image is running and the container is created :
```
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                      NAMES
445edc999c16        ibmcom/db2          "/var/db2_setup/lib/…"   3 weeks ago         Up 29 hours         22/tcp, 55000/tcp,	                  mydb2
                 60006-60007/tcp, 
              0.0.0.0:50000->50000/tcp   
```
4.	Next create a database in DB2 by running the below commands :
```
	I.	docker exec -it <container-id>  bash
	II.	su - db2inst1
	III.	db2start
```
 

IV.	db2sampl

 
		
V.	db2

 

VI.	connect to sample

 

5.	List the tables for schema DBTEMP and it will show that an employee table already exists

6.	Now that you have a database running you can use this database to connect to your app connect integration flows by following the steps in the next section.

7.	To connect to this database you can use the below connection properties 
```
	a.	Database : SAMPLE
	b.	Host : localhost / docker container IP
	c.	Port Number: 50000
	d.	Username : db2inst1
	e.	Password:  passw0rd
```
Note : If you want to connect to the DB from another container , you need to use the container IP. You can find the container IP by running the command “ docker inspect <container-id>. In the below config “172.17.0.2” is the IP address.

```

"Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "8d3b4ce7216eb1d988165568f5fb25d9c6a79ea36aed1ffbc3b4f0d91b00c4a3",
                    "EndpointID": "5e98a2bf0151df85e8c264903374574975d90c86085ae97fa42b0b5ca44a91fc",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:11:00:02",
                    "DriverOpts": null
                }
            }
```

## Docker Environment:  Configure DB2 cli and build a docker file for app connect

## Pre-Requisites:
1.	Docker installation
2.	Download/clone git repository
3.	ODBC cli drivers and libraries
4.	Base image of app connect enterprise from docker hub or pre-built image.
5.	Set up the initial-config directory based on the previous steps.


## Objective: Use a custom docker file to build an app connect enterprise image with db2 cli configurations to create an immutable copy.


## Steps:

1.	Pull the public ibmcom/ace image from docker hub  docker pull ibcom/ace or pre-build the ace image by following the steps of the previous section.
2.	Create a new directory : ace-db2
3.	Download the db2clijar files for linux. Link : https://www.ibm.com/support/pages/download-initial-version-115-clients-and-drivers and keep the tar file in the dir ace-db2
4.	Create a new docker file with the below details 
     Filename : dockerfile.acedb2
     
 ```    
from ibmcom/ace:latest

USER root
RUN echo $PWD

RUN mkdir -p /home/aceuser/db2cli_odbc_driver
COPY ibm_data_server_driver_for_odbc_cli_linuxx64_v11.5.tar.gz /home/aceuser/db2cli_odbc_driver
WORKDIR /home/aceuser/db2cli_odbc_driver
RUN echo $PWD
RUN tar -xvzf ibm_data_server_driver_for_odbc_cli_linuxx64_v11.5.tar.gz

USER aceuser

#COPY odbc.ini /home/aceuser/ace-server/odbc.ini
#COPY odbcinst.ini /var/mqsi/odbc/odbcinst.ini
#COPY db2cli.ini /var/mqsi/odbc/db2cli.ini

ENV ODBCINI /home/aceuser/ace-server/odbc.ini
ENV ODBCSYSINI /home/aceuser/initial-config/odbcini/
ENV DB2CLIINIPATH /home/aceuser/initial-config/odbcini/
ENV LD_LIBRARY_PATH /home/aceuser/db2cli_odbc_driver/odbc_cli/clidriver/lib
ENV IE02_PATH=/opt/IBM/ace-11.0.0.5/ie02
WORKDIR /home/aceuser



```


5.	Create a folder structure called initial-config/odbcini and place the below files under ace-db2/initial-config/odbcini

The below configs in the ini files are to connect to the local docker db2 container that was created in the earlier step.
a.	Db2cli.ini
```
[SAMPLE]
Database=SAMPLE
Protocol=TCPIP
Hostname=172.17.0.2
Port=50000
uid=db2inst1
pwd=passw0rd
autocommit=0
TableType=“‘TABLE’,‘VIEW’,‘SYSTEM TABLE’”

```


b.	Odbc.ini
```
;#######################################
;#### List of data sources stanza ######
;#######################################
[ODBC Data Sources]
SAMPLE=IBM DB2 ODBC Driver
;###########################################
;###### Individual data source stanzas #####
;###########################################
;# DB2 stanza
[SAMPLE]
DRIVER=/home/aceuser/db2cli_odbc_driver/odbc_cli/clidriver/lib/libdb2o.so
Description=IBM DB2 ODBC Database
Database=SAMPLE
;##########################################
;###### Mandatory information stanza ######
;##########################################
[ODBC]
InstallDir=/opt/ibm/ace-11/server/ODBC/drivers
UseCursorLib=0
IANAAppCodePage=4
UNICODE=UTF-8
```
c.	Odbcinst.ini
```
;##########################################################################
;# ODBC database driver manager system initialisation file.               #
;##########################################################################
;# It is recommended that you take a copy of this file and then edit the  #
;# copy.                                                                  #
;#                                                                        #
;# 1. Complete the 'Mandatory information stanza' section                 #
;# at the end of the file.                                                #
;#                                                                        #
;##########################################################################

;##########################################
;###### Mandatory information stanza ######
;##########################################

[ODBC]
;# To turn on ODBC trace set Trace=yes
Trace=no
TraceFile=<A Directory with plenty of free space to hold trace output>/odbctrace.out
Threading=2
```

6.	The folder structure and contents should be as below:

 


7.	Navigate to the ace-db2 path in the command line and run the below command to build the docker image. Note the last “.”
```
docker build -t ace-db2:latest --file dockerfile.acedb2 .

Sending build context to Docker daemon   34.6MB
Step 1/15 : from ibmcom/ace:latest
 ---> 1f7537a53e99
Step 2/15 : USER root
 ---> Running in dcb866611caa
Removing intermediate container dcb866611caa
 ---> e17c2e3553b2
Step 3/15 : RUN echo $PWD
 ---> Running in 9d96f336ea1f
/home/aceuser
Removing intermediate container 9d96f336ea1f
 ---> d1fc0e2a063f
Step 4/15 : RUN mkdir -p /home/aceuser/db2cli_odbc_driver
 ---> Running in 12a946177c4c
Removing intermediate container 12a946177c4c
 ---> 8ae868f1268a
Step 5/15 : COPY ibm_data_server_driver_for_odbc_cli_linuxx64_v11.5.tar.gz /home/aceuser/db2cli_odbc_driver
 ---> ae77e7d6a7b1
Step 6/15 : WORKDIR /home/aceuser/db2cli_odbc_driver
 ---> Running in 2b2cb0d9efcb
Removing intermediate container 2b2cb0d9efcb
 ---> 6d48ba870e6a
Step 7/15 : RUN echo $PWD
 ---> Running in 8def70d8144e
/home/aceuser/db2cli_odbc_driver
Removing intermediate container 8def70d8144e
 ---> c4ecf3a7a504
Step 8/15 : RUN tar -xvzf ibm_data_server_driver_for_odbc_cli_linuxx64_v11.5.tar.gz
 ---> Running in 9f4db516f561
odbc_cli/
odbc_cli/clidriver/
odbc_cli/clidriver/Readme.txt
odbc_cli/clidriver/msg/ 

.
.
.
odbc_cli/clidriver/bnd/db2ajgrt.bnd
Removing intermediate container 9f4db516f561
 ---> c180110551b5
Step 9/15 : USER aceuser
 ---> Running in 0d6093f42295
Removing intermediate container 0d6093f42295
 ---> ed6031b5e6ef
Step 10/15 : ENV ODBCINI /home/aceuser/ace-server/odbc.ini
 ---> Running in 55e7d70c16ef
Removing intermediate container 55e7d70c16ef
 ---> a3951a95fdfc
Step 11/15 : ENV ODBCSYSINI /home/aceuser/initial-config/odbcini/
 ---> Running in 3bdd02daee8b
Removing intermediate container 3bdd02daee8b
 ---> 7bcd92b84468
Step 12/15 : ENV DB2CLIINIPATH /home/aceuser/initial-config/odbcini/
 ---> Running in 7ceb7312b130
Removing intermediate container 7ceb7312b130
 ---> 69201853a030
Step 13/15 : ENV LD_LIBRARY_PATH /home/aceuser/db2cli_odbc_driver/odbc_cli/clidriver/lib
 ---> Running in 9d5456234301
Removing intermediate container 9d5456234301
 ---> dd688b7f9a9a
Step 14/15 : ENV IE02_PATH=/opt/IBM/ace-11.0.0.5/ie02
 ---> Running in 9c093fe2dc1e
Removing intermediate container 9c093fe2dc1e
 ---> 3bfc99eddd44
Step 15/15 : WORKDIR /home/aceuser
 ---> Running in b4308ba8aace
Removing intermediate container b4308ba8aace
 ---> dda891f92da4
Successfully built dda891f92da4
Successfully tagged ace-db2:latest

```
8.	Once the image has been built run the below command to start the container and mount the initial-config folder into the container.
```
docker run --name acedb2 -p 7600:7600 -p 7800:7800 -p 7843:7843 --env LICENSE=accept --env ACE_SERVER_NAME=ACESERVER --mount type=bind,src=/{path} /ace-db2/initial-config,dst=/home/aceuser/initial-config ace-db2:latest
```
9.	Once the container has started exec into the container to set test the connectivity.
Docker exec -it <container-id> /bin/bash

10.	Navigate to the folder /opt/ibm/ace-11/server/bin to set the profile 
Command :  “. ./mqsiprofile”


11.	As we haven’t injected the dbparms into the container, set it with the below 
```
Command   “mqsisetdbparms -w /home/aceuser/ace-server -n SAMPLE -u db2inst1 -p passw0rd”
```
12.	To check the connectivity run the below command to confirm that the db2 cli configuration is correct:
```
Command  mqsicvp -n SAMPLE -u db2inst1 -p passw0rd

BIP8290I: Verification passed for the ODBC environment. 

BIP8270I: Connected to Datasource 'SAMPLE' as user 'db2inst1'. The datasource platform is 'DB2/LINUXX8664', version '11.05.0000'.

```


## Openshift Environment:  


## Pre-Requisites:
1.	Openshift cli  installation : https://www.tutorialspoint.com/openshift/openshift_cli.htm. Install the appropriate binary for the OC on the below link https://github.com/openshift/origin/releases/tag/v3.6.0-alpha.2
2.	Openshift user and cluster details

		##  Clusterurl	
		##  Username	
		## Password	
		## Project	
		## Docker url	

3.	Download/clone git repository : https://github.com/ot4i/ace-docker
4.	Base image of app connect enterprise from docker hub . 
Command:  docker pull ibmcom/ace:latest


Objective: To login to the open shift cluster and push the images from the local system.



1)	Login to the open shift cluster and login to the docker registry on openshift.

```
oc login cluster_url --insecure-skip-tls-verify -u username -p password

docker login -u $(oc whoami) -p $(oc whoami -t) docker registry 
```

2)	Create a new project on openshift or select the appropriate openshift project

oc new-project ace-dev

3)	Tag the image and push the image to the open shift cluster
```
docker tag ace-app:$BUILD_ID docker-registry-default.40.68.250.242.nip.io/ace-sbsa/ace_dea:$BUILD_ID

docker push  docker-registry-default.40.68.250.242.nip.io/ace- sbsa/ace_dea:$BUILD_ID
```
4)	Start an IS on open shift cluster and open routes
```
oc project ace-sbsa

oc new-app ace_dea:$BUILD_ID -e LICENSE=accept -e ACE_SERVER_NAME=ACESERVER
```

5)	Expose the application service by creating a route on open shift cluster.
```
oc expose service acedea --name=acedea-webui --port=7600 --generator="route/v1"
oc expose service acedea --name=acedea-http --port=7800 --generator="route/v1"
```
6)	Get the hostname of the routes by running the below commands

I.	Host for the http listener of the integration server – oc get route acedea-http -o=go-template='{{ .spec.host}}'
II.	Host for the webui of the integration server – oc get route acedea-webui -o=go-template='{{ .spec.host}}'


In case the pods don’t start, do the following steps :
```
1.	oc adm policy add-scc-to-user anyuid -z default
2.      securitycontextconstraints.security.openshift.io/privileged added to: ["system:serviceaccount:test:default"]
3.	oc patch dc/<dc-name> --patch '{"spec":{"template":{"spec":{"serviceAccountName": "default"}}}}' -n <project-name>

```

Now you can connect with the hostname of the service.  Fetch the host name with the below command :

```
oc get route acedea-http -o=go-template='{{ .spec.host}}'
oc get route acedea-webui -o=go-template='{{ .spec.host}}'
```
