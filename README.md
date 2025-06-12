# JWalker
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java 8](https://img.shields.io/badge/Java-%3E=%208-blue.svg)](https://adoptium.net/temurin/releases/?os=any&arch=any&version=8)
[![Deploy Status](https://img.shields.io/github/actions/workflow/status/epieffe/jwalker/maven-publish.yml?label=deploy)](https://github.com/epieffe/jwalker/actions/workflows/maven-publish.yml)

An extremely generic Java library for applying *A** and other built-in search algorithms to user-defined graphs.

## Overview
- **Generic**: Suitable for literally any problem that can be solved with a search algorithm
- **Customizable**: Define custom graphs and heuristics
- **Efficient**: The built-in algorithms use an efficient Fibonacci heap, borrowed from [jheaps](https://github.com/d-michail/jheaps)
- **Lightweight**: No external dependencies

The [jwalker-examples](https://github.com/epieffe/jwalker-examples) repository contains example projects to demonstrate how easy it is to use JWalker to solve any search problem.

### 🤌 How to use
Define your own custom graph by creating a class that implements the [Graph](src/main/java/eth/epieffe/jwalker/Graph.java) interface, along with a separate class for the nodes in your graph.

Once you have a graph, you are ready to use the *Dijkstra* or *Breadth-First Search* built-in algorithms to find an optimal path from a starting node to a target node.

If you want to use an informed search algorithm such as *A** or *Best-First Search*, you need to provide a heuristic for your nodes by implementing the [Heuristic](src/main/java/eth/epieffe/jwalker/Heuristic.java) functional interface.

### ⚡Quick example
The following code snippet uses the built-in *A** algorithm to find an optimal solution for an instance of the [N-Puzzle problem](https://en.wikipedia.org/wiki/15_puzzle).

The example graph, node and heuristic classes are borrowed from the [jwalker-examples](https://github.com/epieffe/jwalker-examples) repository.

```java
import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;
import eth.epieffe.jwalker.Visits;

Graph<NPuzzle> graph = NPuzzleGraph.INSTANCE;
Heuristic<NPuzzle> heuristic = NPuzzleHeuristics::manhattanSum;
Visit<NPuzzle> visit = Visits.aStar(graph, heuristic);
NPuzzle node = NPuzzle.newInstance(1, 3, 8, 4, 6, 7, 5, 2, 0);
List<Edge<NPuzzle>> path = visit.run(node);
```

See the implementation of the example classes at [NPuzzle](https://github.com/epieffe/jwalker-examples/blob/main/npuzzle/src/main/java/eth/epieffe/jwalker/example/npuzzle/NPuzzle.java), [NPuzzleGraph](https://github.com/epieffe/jwalker-examples/blob/main/npuzzle/src/main/java/eth/epieffe/jwalker/example/npuzzle/NPuzzleGraph.java) and [NPuzzleHeuristics](https://github.com/epieffe/jwalker-examples/blob/main/npuzzle/src/main/java/eth/epieffe/jwalker/example/npuzzle/NPuzzleHeuristics.java).

### 💾 Installation
If you use Maven, add the following dependency in your `pom.xml` file:
```xml
<dependency>
    <groupId>io.github.epieffe</groupId>
    <artifactId>jwalker</artifactId>
    <version>1.0.1</version>
</dependency>
```

If you use Gradle, add the following dependency in your `build.gradle` file:
```groovy
implementation 'io.github.epieffe:jwalker:1.0.1'
```

## Built-in algorithms
Here we describe the built-in search algorithms. Users might also add new algorithms by implementing the corresponding Java interface.

### 🧠 Visits
A [Visit](src/main/java/eth/epieffe/jwalker/Visit.java) traverses a graph to find a path from a provided node to a target node. Some visits are guaranteed to find a path with the lowest cost possible, while other visits sacrifice path optimality in exchange for efficiency.

#### built-in visits:
- A* ([Wikipedia](https://en.wikipedia.org/wiki/A*_search_algorithm))
- Best First ([Wikipedia](https://en.wikipedia.org/wiki/Best-first_search))
- BFS ([Wikipedia](https://en.wikipedia.org/wiki/Breadth-first_search))
- Dijkstra ([Wikipedia](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm))

### 🔎 Local searches
A [LocalSearch](src/main/java/eth/epieffe/jwalker/LocalSearch.java) starts from one or more randomly generated nodes and navigates the graph until a node deemed optimal is found or a time bound is elapsed. It is used to solve computationally hard optimization problems. Unlike visits, a local search does not return a path, but only one node is returned.

The returned node is not guaranteed to be optimal, subsequent runs might find a better node.

#### built-in local searches:
- Steepest Descent ([Wikipedia](https://en.wikipedia.org/wiki/Gradient_descent))
