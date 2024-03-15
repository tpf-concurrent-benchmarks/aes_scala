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
N_THREADS=4 sbt run
```

Keep in mind that if `N_THREADS` environment variable is not set, the program will exit with an error message.

Also, you must create a `test_file/lorem_ipsum.txt` file with the text you want to encrypt/decrypt. 
The output of the encryption/decryption will be in the `test_file/output.txt` and `test_file/decrypted.txt `files.

## Running tests

To run the tests, you can use the following command:

```bash
sbt test
```

## Running all services with Docker

```bash
docker compose -f=docker-compose-dev.yml up
```

## Number of threads

If you wish to change the number of threads being used in the local thread pool, you can do so by changing the `N_THREADS` constant in the `Makefile` file.

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
