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
* Javascript
  * React
  * ES6
  * Redux
  * Babel
  * Webpack


# System requirements

### Running locally
* docker
* docker-compose
* Java 17

### Deploy to cloud
* Java 17
* Terraform
* Ansible


# Gradle properties

The `gradle.properties` file contains app secrets and so isn't checked into the repository.  Define these values:

    pricechartingToken={}
    doToken={}
    doSshId={}
    discordToken={}

`pricechartingToken` and `discordToken` are optional, and starting the app without them skips initializing those features.
`doToken` and `doSshId` are both required DigitalOcean properties only if the app is to be deployed to a production server.

# Run locally

## Server
To run the app locally, run these commands:

    ./gradlew ktor-config shadowJar
    docker-compose up

The app is not built in docker, instead, the build output folder is mounted to the container so that it
can be restarted without rebuild.  Ktor's application.conf is programmatically generated using a Handlebars template, 
this allows Terraform to lookup real infrastructure properties at build time, allowing the production artifact to be
fully bundled in a single jar.

My typical developer flow is:

    docker-compose down && ./gradlew ktor-config shadowJar && docker-compose up

## Client
The client is served from the resources/static folder.  To build the client, cd into the `client` directory and
run

    npm i
    npm run build

Then invoke the gradle command:

    ./gradlew package

# Deploy

Deployment to DigitalOcean is automated using Terraform and Ansible, both tools are delegated through Gradle via Gradle tasks.

To begin the deployment process, ensure that Ktor's `application.conf` file has been generated in the resources/ 
directory by invoking Gradle's `ktor-config` task, then build a jar:

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

# Discord Bot details

Available commands:

| command                            | details                                                                                                                |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| ping                               | Responds "pong"                                                                                                        |
| math                               | Evaluates math expressions                                                                                             |
| join                               | If the user is in a voice channel, the bot will join the same                                                          |
| play {youtubeUrl}                  | Instructs the bot to stream audio from a Youtube url to the current voice channel                                      |
| friendcode                         | lookup and display the user's friend code, or another user's using @mention                                            |
| friendcode add {SW-XXXX-XXXX-XXXX} | Add your friendcode so friends can lookup your friend code                                                             |
| friendcode remove                  | Remove your own friend code from the bot's memory                                                                      |
| mtg                                | Lookup magic cards by name on scryfall and print their details and images                                              |
| pokedex                            | Lookup pokedex entries on pokeapi and print their details and images                                                   |
| vg                                 | Lookup loose video game prices by name on pricecharting                                                                |
| roll {int}d{int}                   | Roll a number of n sided dice.  First number is quantity of die, second number is faces on each die.  Defaults to 1D20 |