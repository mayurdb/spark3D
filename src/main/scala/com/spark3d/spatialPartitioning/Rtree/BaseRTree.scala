/*
 * Copyright 2018 Mayur Bhosale
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
package com.astrolabsoftware.spark3d.spatialPartitioning.Rtree

import com.astrolabsoftware.spark3d.geometryObjects.BoxEnvelope

import scala.math._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

/**
  * Rtree indexes the objects based on their minimum bounding rectangle. At its leaf level,
  * each of the rectangles will bound a single object. In the next level of the tree, nearby
  * objects would be grouped together and get represented by their own minimum bounding rectangle.
  * All the bounding boxes except the root node maintain certain minimum fill
  * (number of objects contained) to improve the performance.
  *
  * @param maxNodeCapacity maximum number of elements per node
  */
class BaseRTree (private val maxNodeCapacity: Int = 10){

  def this() {
    this(10)
  }

  var objectList: List[BoxEnvelope] = _
  var root: NonLeafNode = _
  var built: Boolean = false

  /**
    * Insert the object inside the RTree
    */
  def insert(objList: List[BoxEnvelope]): Unit = {
    objectList = objList
  }

  /**
    * Build the RTree in bottom-up manner.
    */
  def build(): Unit = {

    if (built) {
       return
    }

    root = if (objectList.isEmpty) {
      new NonLeafNode(0)
    } else {
      //build the tree
      buildUpperRTree(objectList.map(x => new LeafNode(x.getEnvelope)), -1)
    }

    built = true
  }

  /**
    * Progressively builds the level of the RTree from the children nodes. Once a level is built,
    * the newly created parents are considered as the children and a level is built on top of it.
    *
    * @param levelNodes children for which the level is to be built
    * @param level height from the bottom of the level to be built
    */
  private def buildUpperRTree(levelNodes: List[Node], level: Int): NonLeafNode = {

    val levelParents = constructParents(levelNodes, level+1)

    if (levelParents.size == 1) {
      return levelParents(0).asInstanceOf[NonLeafNode]
    }

    buildUpperRTree(levelParents, level+1)
  }

  /** Creates a level of the RTree by mapping the childeren to the appropriate parent. First,
    * children are soeted based in their X-coordinate and accordingly put in a child bucket while
    * abiding to the max capacity constraint.
    *
    * @param children children for which the level is to be built
    * @param level height from the bottom of the level to be built
    * @return
    */
  private def constructParents(children: List[Node], level: Int): List[Node] = {

    var parents = ListBuffer[Node]()

    parents += new NonLeafNode(level)

    val minLeafCount = max(ceil(children.size / maxNodeCapacity).toInt, 1)

    val sortedChildren = children.sortWith((x, y) =>
        x.envelope.center.x < y.envelope.center.x)

    val parentSlices = verticalSlices(sortedChildren, ceil(sqrt(minLeafCount)).toInt)
    createParentFromChildSlices(parentSlices, level)

  }

  /**
    * Maps children inside each slice from the list slices to a parent.
    *
    * @param slices slices created based on the x-coordinate
    * @param level height from the bottom of the level to be built
    */
  def createParentFromChildSlices(slices: List[List[Node]], level: Int): List[Node] = {
    val parents = ListBuffer[Node]()
    for (i <- slices) {
      parents ++= createParentFromSlice(i, level)
    }
    parents.toList
  }

  /**
    * Maps children inside one slice to a parent.
    *
    * @param slice individual slice created based on the x-coordinate
    * @param level height from the bottom of the level to be built
    */
  def createParentFromSlice(slice: List[Node], level: Int): List[Node] = {
    val parents = ListBuffer[NonLeafNode]()
    parents += new NonLeafNode(level)
    val it = slice.iterator
    while (it.hasNext) {
      val parent = it.next
      if (parents.last.children.size == maxNodeCapacity) {
        parents += new NonLeafNode(level)
      }
      parents.last.children += parent
    }
    parents.toList
  }

  /**
    * Separates the input objects/children into bucket/slices bound by maximum capacity.
    *
    * @param children children to be divided into slices
    * @param sliceCount number of slices into which children are to be divided
    */
  def verticalSlices(children: List[Node], sliceCount: Int): List[List[Node]] = {
    val sliceCapacity = (ceil(children.size) / sliceCount.asInstanceOf[Double]).asInstanceOf[Int]
    val it = children.iterator
    val slices = new Array[ListBuffer[Node]](sliceCount)
    for (i <- 0 until sliceCount) {
      slices(i) = ListBuffer[Node]()
      var added = 0
      while (it.hasNext && added < sliceCapacity) {
        val temp = it.next
        slices(i) += temp
        added += 1
      }
    }

    slices.toList.map(x => x.toList)
  }

  /**
    * Returns the leaf nodes of the Rtree.
    */
  def getLeafNodes(): List[BoxEnvelope] = {
    val leafNodes = ListBuffer[BoxEnvelope]()
    getLeafNodes(root, leafNodes)
    leafNodes.toList
  }
  /**
    * Perform a Breadth First Search Traversal (BFS) of the tree.
    *
    * @param func anonymous function to decide if the desired action should be performed on the this
    *             node or not
    * @param data a ListBuffer in which the desired data should be placed when the funct() == true
    * @param actionID -3 => get the bounding box of the node if the node and the query object
    *                         intersect
    *                 -2 => collect data in the all nodes
    *                 -1 => get the bounding box of the node
    *                 x, where x > 0 => assign partitionID to the leaf node
    */
  private def bfsTraverse(func: Node => Boolean, data: ListBuffer[BoxEnvelope],
                          actionID: Int, obj: BoxEnvelope): Unit = {

    // create a queue
    val que = new Queue[Node]
    // insert the root node
    que += this.root
    var partitionID = actionID
    while (!que.isEmpty) {
      val current = que.dequeue
      if (func(current)) {
        if (actionID == -3) {
          if (current.envelope.intersects(obj) ||
            obj.intersects(current.envelope)) {
            data += current.envelope
          }
        } else if (actionID == -2) {
          // get all the elements in the tree
          data ++= current.children.map(x => x.envelope)
        } else if (actionID == -1) {
          // add this to the leaf node
          data += current.envelope
        } else {
          // assign the partitionID for this node
          current.envelope.indexID = partitionID
          partitionID += 1
        }
      }

      if (!current.isInstanceOf[LeafNode]) {
        // add children to the queue
        for (child <- current.children) {
          que += child
        }
      }
    }
  }


  private def getLeafNodes(node: Node, leafNodes: ListBuffer[BoxEnvelope]): Unit = {

    if (node.isInstanceOf[LeafNode]) {
      leafNodes += node.envelope
      return
    }

    for (child <- node.children) {
      getLeafNodes(child, leafNodes)
    }
  }

  def assignPartitionIDs(): Unit = {
    val traverseFunct: Node => Boolean = {
      node => node.isInstanceOf[LeafNode]
    }
    bfsTraverse(traverseFunct, null, 0, null)
  }

  def getMatchedLeafNodes(obj: BoxEnvelope): ListBuffer[BoxEnvelope] = {
    val matchedLeaves = new ListBuffer[BoxEnvelope]
    val traverseFunct: Node => Boolean = {
      node => node.isInstanceOf[LeafNode]
    }
    bfsTraverse(traverseFunct, matchedLeaves, -3, obj)
    matchedLeaves
  }
}
