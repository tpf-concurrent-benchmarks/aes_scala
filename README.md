# AES-128 in Scala

## Installing Scala

This project includes a `cs` (Coursier CLI) binary that can be used to install Scala. To install Scala, run the following command:

```bash
./cs install scala:3.4.0 && cs install scalac:3.4.0
```

## How to build with sbt

The minimum required version of `sbt` is **1.9** (Scala 3.4). Note that you also need to have at least **JDK 11**.
The project can be built with the following commands once you are in the root directory of the desired project:

```bash
sbt compile
```

If you wish to run (and compile) the project, you can do so with the following command:

```bash
N_THREADS=4 REPEAT=2 PLAIN_TEXT=data/lorem_ipsum.txt ENCRYPTED_TEXT=data/encrypted.txt DECRYPTED_TEXT=data/decrypted.txt sbt run
```

## Running tests

To run the tests, you can use the following command:

```bash
sbt test
```

## Running all services with Docker

```bash
docker compose -f=docker-compose-dev.yml up
```

Alternatively, you can use a Makefile command to run the project.

```bash
make deploy_local
```

## Editing .env file

As an example, this repository includes a `.env.example` file that can be used to set the environment variables for the project.
A `.env` file must be created with the same content as the `.env.example` file, and it should be placed in the root directory of the project.

## Makefile

There is a Makefile in the root directory of the project that can be used to build and run the project

- `make build`: builds the project image
- `make deploy_local`: deploys the project locally, alongside with Graphite, Grafana and cAdvisor.
- `make deploy`: deploys (with Docker Swarm) the manager and worker services, alongside with Graphite, Grafana and cAdvisor.
- `make remove`: removes all services (stops the swarm)

## Used libraries

- [MUnit](https://scalameta.org/munit/): testing library
- [Scala parallel collections](https://github.com/scala/scala-parallel-collections): parallel collections to parallelize the encryption and decryption blocks
- [Java DogStatsD Client](https://github.com/DataDog/java-dogstatsd-client): Java library to send metrics to Graphite
