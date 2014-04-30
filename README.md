gdx-tilemap3d
=============

A work-in-progress library to handle management and rendering of a game world composed of 3D "tiles" arranged in a
uniform 3D grid. Tiles can be composed of just simple 1x1x1 cubes or separate models scaled, rotated, and/or combined
at runtime to arrive at a final Tile model mesh.

**This library is still _very much_ a work in progress!**

## Using

build.gradle

    repositories {
        maven { url "http://maven.blarg.ca" }
    }

    dependencies {
        compile "ca.blarg.gdx:gdx-tilemap3d:0.1-SNAPSHOT"
    }

pom.xml

    <repository>
        <id>blarg.ca</id>
        <url>http://maven.blarg.ca</url>
    </repository>

    <dependency>
        <groupId>ca.blarg.gdx</groupId>
        <artifactId>gdx-tilemap3d</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

## License

Distributed under the the MIT License. See LICENSE for more details.
