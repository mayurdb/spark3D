/*
 * Copyright 2018 Julien Peloton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spark3d.geometryObjects

import com.spark3d.geometry.BoxEnvelope
import com.spark3d.geometryObjects.Shape3D._

/**
  * Class for describing a point in 3D space.
  * By default, the input coordinates are supposed euclidean, that is (x, y, z).
  * The user can also work with spherical input coordinates (x=r, y=theta, z=phi)
  * by setting the argument isSpherical=true.
  *
  * @param x : (Double)
  *   Input X coordinate in Euclidean space, and R in spherical space.
  * @param y : (Double)
  *   Input Y coordinate in Euclidean space, and THETA in spherical space.
  * @param z : (Double)
  *   Input Z coordinate in Euclidean space, and PHI in spherical space.
  * @param isSpherical : (Boolean)
  *   If true, it assumes that the coordinates of the Point3D are (r, theta, phi).
  *   Otherwise, it assumes cartesian coordinates (x, y, z).
  *
  */
class Point3D(val x: Double, val y: Double, val z: Double,
    val isSpherical: Boolean) extends Shape3D with Serializable {

  // The center of the point is the point
  val center: Point3D = this

  // Zero radius
  val radius: Double = 0.0

  // Zero volume
  override def getVolume: Double = 0.0

  /**
    * Methods to determine whether two shapes overlap.
    * Implement different ways for different shapes.
    *
    * @param otherShape : (Shape3D)
    *                   An instance of Shape3D (or extension)
    * @return (Boolean) true if the two objects intersect.
    *
    */
  override def intersect(otherShape: Shape3D): Boolean = {

    // Different methods to handle different shapes
    // Keep in mind a point is a sphere with radius 0.
    if (otherShape.isInstanceOf[Sphere] | otherShape.isInstanceOf[Point3D]) {
      sphereSphereIntersection(this, otherShape)
    } else {
      throw new AssertionError(
        """
        Cannot perform intersection because the type of shape is unknown!
        Currently implemented:
          - sphere x sphere
          - sphere x point
          - point  x point
        """)
    }
  }

  /**
    * Get bounding box of this Point3D which will also be a Point3D
    *
    * @return Cuboid representing the box of the Point3D
    */
  override def getEnvelope(): BoxEnvelope = {
    new BoxEnvelope(this)
  }

  /**
    * Returns the distance between the point and another.
    * Space is supposed flat (euclidean).
    *
    * @param p : (Point3D)
    *          Another instance of Point3D
    * @return (Double) Distance between the two points.
    *
    */
  def distanceTo(p: Point3D): Double = {
    val module = if (!this.isSpherical) {
      math.sqrt(
        (this.x - p.x) * (this.x - p.x) +
          (this.y - p.y) * (this.y - p.y) +
          (this.z - p.z) * (this.z - p.z)
      )
    } else {
      math.sqrt(
        this.x * this.x + p.x * p.x - 2 * this.x * p.x * (
          math.sin(this.y) * math.sin(p.y) *
            math.cos(this.z - p.z) +
            math.cos(this.y) * math.cos(p.y)
          )
      )
    }
    module
  }

  /**
    * Return the coordinates (x, y, z) of the point.
    *
    * @return (List[Double]) The list of coordinates.
    */
  def getCoordinate: List[Double] = {
    List(this.x, this.y, this.z)
  }

  /**
    * Return if the input Point3D is equal this Point3D
    *
    * @param p Point3D for which the equality is to be checked
    * @return true if the two Point3Ds are equal
    */
  def isEqual(p: Point3D): Boolean = {
    x == p.x &&
    y == p.y &&
    z == p.z
  }
}
