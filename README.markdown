About
=====
Aedile as a Consul configuration K/V importer and exporter in the spirit of `git2consul`

Running the Aedile
==================

Download the `aedile.jar`.

It is a standalone JAR, executable via `java -jar aedile.jar`

There are following functionalities:

*   import to Consul K/V from a specific directory with a set of .properties files
*   import to Consul K/V from Git
*   immport to Consul K/V from a set of datacenter-specific directories
*   export from Consul K/V to the directory tree

Importing from the filesystem
=============================

To import the files from the filesystem directory, run Aedile:

    java -jar aedile.jar import /etc/consul/config/ft-prod

The Consul configuration for the import is guided by the Environment Variables.

By default, all subdirectories are imported as well, recursively.

In the example, `/etc/consul/config/ft-prod/mt` and `/etc/consul/config/ft-prod/meta`
subdirectories will be imported as `mt` and `meta` subdirectories respectively)
under `ft-prod` directory in Consul K/V.

Renaming property files (`--remap-config-name`)
-----------------------------------------------
Property filenames can be remapped:

    --remap-config-name application-local=application,local

This will remap `application-local.property` to `application,local` directory in Consul K/V.

Disabling recursion
-------------------

To disable recursive import, use the `--no-recurse` argument, such as:

    java -jar aedile.jar import --no-recurse /etc/consul/config/ft-prod

Exporting from the Consul K/V to the filesystem
===============================================

To export the whole Consul K/V configuration into the filesystem directory
hierarchy, run Aedile exporter:

    java -jar aedile.jar export /tmp

This will import the root folder and all recursive subfolders to the `/tmp`
folder in the filesystem.

All Consul K/V folders will be mapped to the filesystem directories.
The bottommost folders will be mapped to the `.properties` files, containing
keys and values separated by `=`.

Importing large datacenter structure
====================================
Imports multiple folders that corresponds to datacenter configurations.

To import, run Aedile:

    java -jar aedile.jar import-dc /etc/consul/config/datacenters

Imagine the following folder hierarchy:

    ./datacenters
        |- dc1
        |  |- config
        |     |- unittest
        |     |  |- application
        |     |     |- code.properties
        |     |     |- demo.properties
        |     |- unittest2
        |        |- application
        |           |- demo2.properties
        |- dc2
           |- config
              |- prod
                  |- application.properties
                  |- impl.properties

Importing the `datacenters` directory creates the following structure in Consul
K/V. The `dc1` and `dc2` directories will map to the *dc*
parameter in Consul K/V REST API requests. The first directory `dc1` will map to
the `dc=dc1` query parameter, and the second directory `dc2` will map to the
`dc=dc2` query param.

The datacenter `dc1` will contain the structure that corresponds to the filesystem
structure.

    |- config
    |   |- unittest
    |   |  |- application
    |   |     |- code
    |   |     |   |- key=value from code.properties
    |   |     |   |- another key=value from code.properties
    |   |     |- demo
    |   |         |- key=value from demo.properties
    |   |         |- another key=value demo code.properties
    |   |- unittest2
           |- application
              |- demo2
                  |- key=value from demo2.properties
                  |- another key=value from demo2.properties

### Ignoring Folders
The `CONSUL_IMPORT_EXCLUDE` variable contains a datacenter/folder specification
that shall be ignored upon import.

An example:

    dc1#config/unittest/application:dc1#config/unittest/application2

*   The datacenter `dc1` shall ignore the `config/unittest/application` folder
*   The datacenter `dc1` shall ignore the `config/unittest2/application` folder

#### Wildcard supports

Specification may contain Ant-based wildcards:

    dc1#config/**/*

This ignores any subfolder (recursively) in the `config` folder for datacenter `dc1`.

Git import
==========

To import the files from the Git directory, run Aedile:

    java -jar aedile.jar git config.json

Git config
----------

Git config is configured via `git2consul`-like configuration file.

    {
      "version": "1.0",
      "local_store" : "./target/gitrepo",
      "repos" : [{
        "name" : "aedile",
        "url" : "https://github.com/novotnyr/aedile.git",
        "source_root": "src/test/resources/datacenters/dc1/config/unittest/application",
        "mountpoint": "config"
      }]
    }

*   `version`: always 1.0
*   `local_store`: the local temporary file that will hold the cloned Git repo
*   `name`: name of the Git repo. Will be appended to the Consul K/V prefix.  
    When building the key name, git2consul will concatenate mountpoint, repo
    name, and the path of the file in your git repo.
*   `url`: URL of the Git repo
*   `source_root`: A `source_root` is a repo-level option instructing Aedile to
    navigate to a subdirectory in the git repo before mapping files to K/Vs. By
    default, Aedile mirrors the entire repo into Consul KVs.  
    If you have a repo configured with the `source_root`
    `src/test/resources/datacenters/dc1/`, the file
    `src/test/resources/datacenters/dc1/config/unittest/application/config.properties` would be
    mapped to the KV as `config/unittest/application/config.properties`.

Environment Variables
=====================

*   `CONSUL_ENDPOINT` represents a Consul agent host. It is `localhost` by default.
*   `CONSUL_PORT` represents a Consul agent port. It is 8500 by default.
*   `TOKEN` corresponds to the ACL token as specified by Consul ACL protection. It is empty by default.
*   `CONSUL_HTTP_USER` represents a user that is sent in the HTTP Basic Authentication with HTTP requests to Consul API.
*   `CONSUL_HTTP_PASSWORD` represents a password that is sent in the HTTP Basic authentication with HTTP requests.
*   `CONSUL_KEY_PREFIX` represents a default prefix for the configuration values.
*   `CONSUL_DATACENTER` represents a datacenter that is targeted with requests.
*   `CONSUL_IMPORT_EXCLUDE` contains a specification of ignored datacenters and subfolders
