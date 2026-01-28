# Infrastructure Network Analyzer

A console application that builds an undirected, unweighted network graph from a tab-separated input file and provides analytics like connection counts, node groups by attribute, closeness centrality, and connector nodes.

## Demo

1. **Initial Load and Menu:**

	![Initial Load and Menu](media/Initial.png)

2. **Graph Ready State:**

	![Graph Ready State](media/GraphReady.png)

## Files
- [Network_Analyzer.java](Network_Analyzer.java): Main program and graph implementation.
- [input.txt](input.txt): Example input data file (TSV).

## Requirements
- Java JDK (11+ recommended).

## Build and Run
- Compile:

```bash
cd /workspaces/Network_Analyzer
javac Network_Analyzer.java
```

- Run:

```bash
java Network_Analyzer
```

When prompted, enter the path to your TSV input file (for example, `input.txt`).

## Input Format (TSV)
- Header line followed by one line per node.
- Columns:
	1. `id` (long)
	2. `name1`
	3. `name2`
	4. `group` (e.g., college)
	5. `type` (e.g., department)
	6. `email`
	7. `connectionCount` (int)
	8. `connectionID1` ... `connectionIDN` (exactly `connectionCount` ids)

```
id	name1	name2	group	type	email	connectionCount	connectionID1	connectionId2
1	Alpha	NodeA	"Engineering"	ECE	alpha@network.com	2	2	3
2	Beta	NodeB	"Engineering"	ME	beta@network.com	1	1
```

```
3	Gamma	NodeC	"Arts & Sciences"	Math	gamma@network.com	1	1
```

Notes:
 - Ensure `connectionCount` matches the number of following IDs on the line.
- `group` values may be quoted in the input; quotes are handled.
- Connections are treated as undirected and added only once.

## Features
- Remove connection: deletes an undirected edge between two nodes.
- Delete node: removes a node and all incident connections.
- Count connections: shows count and lists direct neighbors.
- Node group: lists connected groups filtered by `group`.
- Closeness centrality: computes and shows raw and normalized scores.
- Find connectors: identifies articulation points in the graph.

## Usage Tips
- Keep node ids unique across the file.
- Ensure `connectionCount` matches the number of following connection ids on the line.
- Use the menu to perform operations after the file loads.
