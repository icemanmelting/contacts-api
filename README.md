# contacts-api

This project was created to build a small api to create/edit contacts and add entries to existing contacts with phone numbers

# Usage

In order to use, first you need to deploy a local postgres instance, and create a database called `contacts_api_test`(check resources `db.edn` for more details, 
like user and password).

After creation, run `postgresql-schema.sql` to deploy the schema needed. You can also check `dev-seed.sql`, to check the seed data,
used to create a simple user(user creation routes were not added due to time constraints) and the first document.

The project contains a docker file and another file called `dock.sh`. This file will create an uberjar and deploy it to docker automatically. The only issue with this approach,
is that since the pg instance will be running outside the container, we will need to point to a known address besides localhost (since localhost will be the container's own ip).

This can be solved by adding `--net="host"` to the docker run command, or simply by using an external database. Note that this wouldn't be a problem for a staging or production environments,
since usually the db is running on a completely different machine, and configured accordingly in the `db.edn` file, located in the resources folder of the project.

# Production deployment

In order to deploy to production, you need to change the db.edn configuration, and edit the server's url, the database name, possible port, user and password. Also, in the machine you will be using, 
if the docker file provided is not used, then you will need to set an environment variable with the name of the environment you are deploying (production. staging, etc), this will tell the configurator,
which path to take on the configuration file. The name of this variable is `CONTACTS_API_ENV`, so for instance, in a Ubuntu distro, to setup the production config, before running the server, you would do:

```
export CONTACTS_API_ENV=production
```

The same can be done for staging:

```
export CONTACTS_API_ENV=staging
```

Note: if the variable is left blank, it will take the default values from the config, which in this case means checking localhost:5432 for the PG server, and look for a db named `contacts_api_test`
with the username `postgres` and paswword `postgres`.