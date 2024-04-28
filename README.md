# AES-128 in Scala

## Objective

This is a Scala implementation of the [Advanced Encryption Standard (AES)](https://nvlpubs.nist.gov/nistpubs/fips/nist.fips.197.pdf).

The objective of this project is to analyze the concurrent capabilities of Scala, under various workloads, and to compare the performance between different languages.

## Deployment

### Requirements

- [Docker](https://www.docker.com/)
- [Scala](https://www.scala-lang.org/) (for local development & testing)

#### Installing Scala

This project includes a `cs` (Coursier CLI) binary that can be used to install Scala. To install Scala, run the following command:

```bash
./cs install scala:3.4.0 && cs install scalac:3.4.0
```

#### How to build with sbt

The minimum required version of `sbt` is **1.9** (Scala 3.4). Note that you also need to have at least **JDK 11**.
The project can be built with the following commands once you are in the root directory of the desired project:

```bash
sbt compile
```

If you wish to run (and compile) the project, you can do so with the following command:

```bash
N_THREADS=4 REPEAT=2 PLAIN_TEXT=data/lorem_ipsum.txt ENCRYPTED_TEXT=data/encrypted.txt DECRYPTED_TEXT=data/decrypted.txt sbt run
```

### Configuration

The configuration is done through the `Makefile` constants. The following variables are available:

- `N_THREADS`: Number of threads to be used in the encryption process
- `REPEAT`: Number of times the encryption/decryption process will be repeated
- `PLAIN_TEXT`: Path to the file with the data to be encrypted
- `ENCRYPTED_TEXT`: Path to the file where the encrypted data will be stored
- `DECRYPTED_TEXT`: Path to the file where the decrypted data will be stored

> Having a `PLAIN_TEXT` and `ENCRYPTED_TEXT` will mean encrypting the data, while having a `ENCRYPTED_TEXT` and `DECRYPTED_TEXT` will mean decrypting the data. Having all three will mean encrypting and decrypting the data.

### Running

#### Setup

- `docker swarm init`: initializes docker swarm
- `make build`: builds the project image
- `make dummy_file`: Creates a dummy with data to be encrypted

#### Run

- `make deploy`: deploys (with Docker Swarm) the manager and worker services, alongside with Graphite, Grafana and cAdvisor.
- `make remove`: removes all services (stops the swarm)
- `make logs`: shows the logs of the services

#### Tests

- `sbt test`: runs the tests

## Libraries

- [MUnit](https://scalameta.org/munit/): testing library
- [Scala parallel collections](https://github.com/scala/scala-parallel-collections): parallel collections to parallelize the encryption and decryption blocks
- [Java DogStatsD Client](https://github.com/DataDog/java-dogstatsd-client): Java library to send metrics to Graphite
