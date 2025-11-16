# JWalker
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java 8](https://img.shields.io/badge/Java-%3E=%208-blue.svg)](https://adoptium.net/temurin/releases/?os=any&arch=any&version=8)
[![Deploy Status](https://img.shields.io/github/actions/workflow/status/epieffe/jwalker/maven-publish.yml?label=deploy)](https://github.com/epieffe/jwalker/actions/workflows/maven-publish.yml)

An extremely generic Java library for applying *A** and other built-in search algorithms to user-defined graphs.

## 📖 Overview
- **Generic**: Suitable for literally any search problem
- **Customizable**: Define custom graphs and heuristics
- **Efficient**: The built-in algorithms are implemented wisely
- **Lightweight**: No external dependencies

See the [jwalker-examples](https://github.com/epieffe/jwalker-examples) repository for some cool example projects.

## 🛠️ How to use
To start using the built-in search algorithms, you need an object that implements the [Graph](src/main/java/eth/epieffe/jwalker/Graph.java)
interface.

The built-in [SimpleGraph](src/main/java/eth/epieffe/jwalker/SimpleGraph.java) class can be used to create a
simple graph object for trivial use cases or quick prototypes, while for more advanced use cases it is recommended to
define your own class implementing the `Graph` interface.

Once you have a `Graph`, you are ready to use the built-in graph search algorithms. If you want to use an informed search
algorithm, such as A*, you also need to provide a heuristic for your nodes by implementing the [Heuristic](src/main/java/eth/epieffe/jwalker/Heuristic.java)
functional interface.

You are free to use any class to represent the nodes in your graph, as long as the `equals` and `hashCode` methods are
implemented correctly and the `hashCode` value of a node object does not change over its lifetime.

### Built-in SimpleGraph class
The built-in `SimpleGraph` class can be used to quickly create a simple graph without the need to implement the `Graph`
interface yourself.

```java
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.SimpleGraph;

import java.net.URI;
import java.util.List;

URI google = URI.create("https://www.google.com");
URI wikipedia = URI.create("https://en.wikipedia.org");
URI github = URI.create("https://github.com");
URI reddit = URI.create("https://www.reddit.com");

Graph<URI> graph = SimpleGraph.<URI>builder()
        .addEdge(google, wikipedia)
        .addEdge(google, github)
        .addEdge(google, reddit)
        .addEdge(wikipedia, google)
        .addEdge(github, wikipedia)
        .addEdge(reddit, github)
        .addEdge(reddit, wikipedia)
        .build();
```

Edges can also be created with a weight and an optional label.

```java
Object node1 = new Object();
Object node2 = new Object();
Graph<Object> graph = SimpleGraph.<Object>builder()
        .addEdge(node1, node2, 2.5, "Label")
        .build();
```

### Custom Graph implementation
You can easily define your own class that implements the `Graph` interface.

The following example defines a 2D grid, where some cells are walkable, while other cells are obstacles and cannot be traversed.
Having our grid implement the `Graph` interface enables us to use the built-in search algorithms to find the shortest path
between two cells.

```java
import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A cell in the grid.
 * Used to represent nodes in our graph.
 */
public record Cell(int row, int col) {}

/**
 * A 2D grid that implements the Graph interface.
 * Some cells are walkable, while others are obstacles.
 */
public class Grid implements Graph<Cell> {
    private static final String[] DIR_LABEL = {"LEFT", "RIGHT", "UP", "DOWN"};
    private static final int[] DIR_ROW = {0, 0, -1, 1};
    private static final int[] DIR_COL = {-1, 1, 0, 0};

    // Cells marked as true are walkable, while false cells are obstacles
    private final boolean[][] grid;

    public Grid(boolean[][] grid) {
        this.grid = grid;
    }

    /**
     * Returns the edges that connect the provided Cell to its walkable neighbours.
     */
    @Override
    public List<Edge<Cell>> outgoingEdges(Cell cell) {
        List<Edge<Cell>> edges = new ArrayList<>();
        for (int i = 0; i < DIR_LABEL.length; i++) {
            int newRow = cell.row() + DIR_ROW[i];
            if (newRow < 0 || newRow >= grid.length) continue;
            int newCol = cell.col() + DIR_COL[i];
            if (newCol < 0 || newCol >= grid[newRow].length) continue;
            if (grid[newRow][newCol]) {
                edges.add(new Edge<>(new Cell(newRow, newCol), 1, DIR_LABEL[i]));
            }
        }
        return edges;
    }
}
```

### Built-in algorithms
The built-in algorithms operate on `Graph` objects.

The following example finds the shortest path between two cells in a Grid using the Dijkstra algorithm. The `Grid` class
is defined in the previous section.

```java
import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Visit;
import eth.epieffe.jwalker.Visits;

import java.util.List;

Graph<Cell> grid = new Grid(new boolean[][] {
        {true, true, false, true},
        {true, false, true, true},
        {true, true, true, false},
        {false, true, true, true}});
Cell start = new Cell(0, 0);
Cell target = new Cell(3, 3);
Visit<Cell> dijkstra = Visits.dijkstra(grid, target);
List<Edge<Cell>> path = dijkstra.run(start);
```

To use an informed search algorithm, such as A*, you also need to provide a heuristic. The following example defines the
Manhattan distance heuristic for a Cell in a Grid and uses the A* algorithm to find the shortest path.

```java
Graph<Cell> grid = new Grid(...);
Cell start = new Cell(0, 0);
Cell target = new Cell(3, 3);
Heuristic<Cell> manhattan = cell ->
        Math.abs(cell.row() - target.row()) + Math.abs(cell.col() - target.col());
Visit<Cell> aStar = Visits.aStar(grid, manhattan);
List<Edge<Cell>> path = aStar.run(start);
```

Optioally, you can provide a callback that will be called on each explored node when it is visited.

```java
Visit<Cell> visit = Visits.dijkstra(grid, target);
List<Edge<Cell>> path = visit.run(start, cell -> {
    System.out.printf("Visiting cell (%d, %d)\n", cell.row(), cell.col());
});
```

## 💾 Installation
If you use Maven, add the following dependency in your `pom.xml` file:
```xml
<dependency>
    <groupId>io.github.epieffe</groupId>
    <artifactId>jwalker</artifactId>
    <version>1.1.0</version>
</dependency>
```

If you use Gradle, add the following dependency in your `build.gradle` file:
```groovy
implementation 'io.github.epieffe:jwalker:1.1.0'
```

## 🧠 Built-in algorithms
Here we describe the built-in search algorithms. Users might also add new algorithms by implementing the corresponding Java interface.

### Visits
A [Visit](src/main/java/eth/epieffe/jwalker/Visit.java) traverses a graph to find a path from a provided node to a target node. Some visits are guaranteed to find a path with the lowest cost possible, while other visits sacrifice path optimality in exchange for efficiency.

#### built-in visits:
- A* ([Wikipedia](https://en.wikipedia.org/wiki/A*_search_algorithm))
- Dijkstra ([Wikipedia](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm))
- Best First ([Wikipedia](https://en.wikipedia.org/wiki/Best-first_search))
- IDA* ([Wikipedia](https://en.wikipedia.org/wiki/Iterative_deepening_A*))
- Parallel IDA* ([Research paper](https://en.wikipedia.org/wiki/Parallel_iterative_deepening_A*))
- IDDFS ([Wikipedia](https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search))
- BFS ([Wikipedia](https://en.wikipedia.org/wiki/Breadth-first_search))

### Local searches
A [LocalSearch](src/main/java/eth/epieffe/jwalker/LocalSearch.java) starts from one or more randomly generated nodes and navigates the graph until a node deemed optimal is found or a time bound is elapsed. It is used to solve computationally hard optimization problems. Unlike visits, a local search does not return a path, but only one node is returned.

The returned node is not guaranteed to be optimal, subsequent runs might find a better node.

#### built-in local searches:
- Steepest Descent ([Wikipedia](https://en.wikipedia.org/wiki/Gradient_descent))
