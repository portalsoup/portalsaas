My personal web app and Discord bot.

This application is both a place for me to experiment with new ideas
and be a stable place to deploy personally useful code.

# Tech stack

* Kotlin
  * Ktor-server (Web server)
  * Ktor-client (Web client)
  * Discord4j (Discord API bindings)
  * Exposed (SQL library)
* Infrastructure
  * Gradle
  * Docker
  * docker-compose
  * Postgresql
  * Terraform
  * Ansible
  * DigitalOcean


# System requirements

### Running locally
* docker
* docker-compose
* Java 17

### Deploy to cloud
* Java 17
* Terraform
* Ansible


# Required properties

The Gradle.properties file contains app secrets and so isn't checked into the repository.  

    pricechartingToken={}
    doToken={}
    doSshId={}
    discordToken={}

`pricechartingToken` and `discordToken` are optional, and starting the app without them simply won't initialize those features.
`doToken` and `doSshId` are both required DigitalOcean properties if the app is to be deployed to a production server.

# Run locally
To run the app locally, run these commands:

    ./gradlew ktor-config shadowJar
    docker-compose up

The app is not built in docker, instead, the build output folder is mounted to the container so that it
can be restarted without rebuild.  Ktor's application.conf is programmatically generated using a Handlebars template, 
this allows Terraform to lookup real infrastructure properties at build time, allowing the production artifact to be
fully bundled in a single jar.

My typical developer flow is:

    docker-compose up -d db   # start the database in detached mode

    ./gradlew shadowJar && docker-compose run --rm app    # ctrl+c then re-run to rebuild

# Deploy

Deployment is performed using Terraform and Ansible, both tools are delegated through Gradle via Gradle tasks.

To being the deployment process, ensure both a valid production Ktor `application.conf` file exists in the resources/ 
directory, then build a jar:

    ./gradlew ktor-config shadowJar

Once this artifact is built and tested, it can be deployed using:

    ./gradlew deploy


This deploy task will walk you through multiple Terraform and Ansible operations, including displaying the calculated 
`terraform plan` output and requiring you to accept on the command line before performing any destructive operations.


# Environments

## Local

The local environment is run across 2 docker containers:
* app
* db

`app` is an alpine container running Java that runs the file `/app/shadow.jar`, this file is mounted from the host using
the jar currently in the build output folder.  The JDBC connection details are overridden in the project's `docker-compose.yaml`
file, and are favored over `application.yaml` whenever present.

## Production

Production consists of the following DigitalOcean resources:
* A droplet running the app
* A Postgresql cluster

The app is loaded into a systemd using an init script which handles auto-running and restarts 