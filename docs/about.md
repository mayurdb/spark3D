---
layout: posts
title: About
permalink: /about/
author_profile: true
---

## Why spark3D?

The volume of data recorded by current and
future High Energy Physics & Astrophysics experiments,
and the complexity of their data set require a broad panel of
knowledge in computer science, signal processing, statistics, and physics.
Exact analysis of those data sets produced is a serious computational challenge,
which cannot be done without the help of state-of-the-art tools.
This has to be matched by sophisticated and robust analysis performed on many
powerful machines, as we need to process or simulate several times data sets.

While a lot of efforts have been made to develop cluster computing systems for
processing large-scale spatial 2D data
(see e.g. [GeoSpark](http://geospark.datasyslab.org)),
there are very few frameworks to process and analyse 3D data sets
which were hitherto too costly to be processed efficiently.
With the recent development of fast and general engine such as
[Apache Spark](http://spark.apache.org), taking advantage of
distributed systems, we enter in a new area of possibilities.
[spark3D](https://github.com/astrolabsoftware/spark3D) extends Apache Spark to
efficiently load, process, and analyze large-scale 3D spatial data sets across machines.

## Goals of spark3D

We have to distribute the computation for a very large amount of 3D data.
Several goals have to be undertaken in this project:

- construct a highly scalable architecture so as to accept very large dataset. (typically greater than what can be handled by practical memory sets)
- datasets are sets of 3D data, ie. object data containing both 3D information plus additional related data.
- mechanisms should offer:
  + indexing mechanisms
  + ways to define a metric (ie. distance between objects)
  + selection capability of objects or objects within a region
- work with as many input file format as possible (CSV, ROOT, FITS, HDF5 and so on)
- package the developments into an open-source library.

## Support

![LAL]({{ "/assets/images/lal_logo.jpg" | absolute_url }})
![UPSUD]({{ "/assets/images/Logo_Université_Paris-Saclay.svg.png" | absolute_url }})
![CNRS]({{ "/assets/images/1012px-Centre_national_de_la_recherche_scientifique.svg.png" | absolute_url }})
