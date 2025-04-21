## Prerequisites

*   [Docker](https://www.docker.com/products/docker-desktop/) installed and running
*   Wildfly 26.1.3.Final - Download from [GitHub](https://github.com/wildfly/wildfly/releases/download/26.1.3.Final/wildfly-26.1.3.Final.zip) or the [Wildfly Downloads page](https://www.wildfly.org/downloads/)
*   IntelliJ IDEA Ultimate (explanations assume usage of this IDE)
*   This is using Jakarta EE 8, meaning that the imports you see are javax instead of jakarta

## Docker Compose for Postgres DB

Used for running the Postgres database:

*   To start up: `docker compose up`
*   To stop: `docker compose down`

Using the docker compose file, you will be able to get credentials for the connection string.

## IntelliJ Setup

How the setup can look like in IntelliJ:

![image](https://github.com/user-attachments/assets/c8320fdf-0ba1-4fda-b1ab-a6a5dab1c082)

## Wildfly Setup

*   It is expected that a datasource for the Postgres DB is created with the JNDI name `"java:/CoffeeBeanDS"`.
*   If it is the first time setting up, you will likely need to install the Postgres driver. This guide might help: [Install PostgreSQL JDBC Driver as a Wildfly Module](https://ralph.blog.imixs.com/2016/10/22/wildfly-install-postgresql-jdbc-driver-as-a-module/)
*   The easiest way to set this up is through the Wildfly admin console at [http://localhost:9990/console/](http://localhost:9990/console/).

![image](https://github.com/user-attachments/assets/93ba8740-37f4-4414-acf3-4a1c3c51e2b3)

*   This console requires an admin user, which needs to be created beforehand. See [How to access Wildfly admin console](https://www.mastertheboss.com/jbossas/jboss-configuration/how-to-access-wildfly-admin-console/) for guidance.
*   Navigate in the admin console: `Configurations` -> `Subsystems` -> `Datasources & Drivers` to check that the Postgres driver is available.
![image](https://github.com/user-attachments/assets/0baa53ae-a47a-4181-990b-342cca8cae56)

My driver version:

![image](https://github.com/user-attachments/assets/6b995cff-cf16-4506-92da-6be767bda2f9)

And the datasource itself:

![image](https://github.com/user-attachments/assets/9347437e-0874-44dd-9de7-46931ea1bd78)

*   If Wildfly is not already set up within IntelliJ, that will also need to be done. You may need to search online for specific steps for your IntelliJ version.

## Running the Application

When running the application, you should be able to see SQL commands in the terminal like this:

![image](https://github.com/user-attachments/assets/46a87d95-7a85-4944-a7c3-ecd467593c11)

## `persistence.xml` Configuration

In the `persistence.xml` file, you will need to change settings to these, otherwise the tables will be dropped each time the app is run:

![image](https://github.com/user-attachments/assets/9e7ca129-8e91-409c-b7ae-2a7647f45725)
