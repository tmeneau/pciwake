# PCI Wake

## Abandoned

WArning: this project has been abandoned and is not under active development!

## Overview

An open source web application developed as a solution to the PCI (and possibly
other audit) requirement to perform periodic log reviews. The PCI Wake project
allows consumers to configure any number of automated log reviews against any
number of configured "log managers" (such as Graylog) with the following

1. log the executed log review if no log incidents occurred requiring manual
review; or
2. log the executed log review and notify reviewers of any log incidents that
require attention.
3. provide a central location for reviewers to update the review status of
specific incidents, and generate an audit trail of periodic review completion
(whether through step (1) or (2) above)

Note that this project is built with a very specific purposes in mind:
facilitating the generation of auditable evidence of automated (and, when
necessary, manual) periodic log reviews to meet audit (specifically PCI)
requirements. While the interpretation of what it means to fulfill this
requirement seems relatively loose, the writers of this project are currently
treating the determination of when a manual review is necessary as being the
concern of the "log manager" and not the "PCI Wake" project.

To put it differently: this project does not perform log analysis; it consumes
the result of applications that perform log analysis. In order for this to be
helpful, consumers will likely need to already have log management solutions in
place that can parse and filter log data into incidents that require review and
expose access to those incidents through an API.

## Quick Start

Copy `./devconfig.example` to `./devconfig`, and then follow one of the
following two:

#### 1. Use Embedded H2 Database

From the project's root directory, run:
```
./gradlew appRun -PembeddedDb
```

The above command will generate an H2 database (stored in ./build/embedded/h2,
so data will be persisted between runs until the next `./gradlew clean` is run).

#### 2. Use External MySQL Database

Alternatively, to spin up a dockerized MySQL container (using jdbc connection
configured in `./devconfig/siteconfig.groovy`) -- assuming you have docker, docker-compose (and boot2docker as applicable) installed and properly
configured -- run the following:

```
docker-compose up -d
./gradlew appRun
```

To change the MySQL JDBC connection parameters, update the `db` connection
parameters in `./devconfig/siteconfig.groovy`.

## Note

PCI Wake as a project is very much still in infancy. While filed issues and pull
requests are welcome, please note that this project is still very much in its
initial SNAPSHOT phase and will be changing as previously un-vetted or
un-considered implementation details are corrected. Use at your own risk; no
warranties are made of PCI Wake's usability, security, functionality, etc...

## Background

This project was undertaken with the failure to locate any existing open source
tools that would query one or more centralized log manager instances for
log events meeting PCI (or any other audit) log review requirements. This came
as a shock (and is still a shock) considering the PCI requirements clearly
seem to indicate the periodic log reviews can be performed periodically.

## License

TODO
