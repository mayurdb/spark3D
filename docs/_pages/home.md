---
layout: splash
permalink: /
header:
  overlay_color: "#5e616c"
  overlay_image: /assets/images/night-sky.jpg
  cta_label: "<i class='fas fa-download'></i> Install Now"
  cta_url: "/docs/installation/"
  caption:
intro:
  - excerpt: '<p><font size="6">Spark extension for processing large-scale 3D data sets: Astrophysics, High Energy Physics, Meteorology, ...</font></p><br /><a href="https://github.com/astrolabsoftware/spark3D/releases/tag/0.1.4">Latest release v0.1.4</a>'
excerpt: '{::nomarkdown}<iframe style="display: inline-block;" src="https://ghbtns.com/github-btn.html?user=astrolabsoftware&repo=spark3D&type=star&count=true&size=large" frameborder="0" scrolling="0" width="160px" height="30px"></iframe> <iframe style="display: inline-block;" src="https://ghbtns.com/github-btn.html?user=astrolabsoftware&repo=spark3D&type=fork&count=true&size=large" frameborder="0" scrolling="0" width="158px" height="30px"></iframe>{:/nomarkdown}'
feature_row:
  - image_path:
    alt:
    title: "<i class='fas fa-upload'></i> Load 3D object RDD"
    excerpt: "Distribute points, spheres, shells, boxes, and more using spark3D."
    url: "/docs/introduction/"
    btn_class: "btn--primary"
    btn_label: "Learn More"
  - image_path:
    alt:
    title: "<i class='fas fa-cubes'></i> Partition your space"
    excerpt: "Partition the three-dimensional space to speed-up your search."
    url: "/docs/partitioning/"
    btn_class: "btn--primary"
    btn_label: "Learn More"
  - image_path:
    alt:
    title: "<i class='fas fa-crosshairs'></i> Query, match, play!"
    excerpt: "Find objects based on conditions, cross-match data sets, and define your requests."
    url: "/docs/query/"
    btn_class: "btn--primary"
    btn_label: "Learn More"
---

{% include feature_row id="intro" type="center" %}

{% include feature_row %}
