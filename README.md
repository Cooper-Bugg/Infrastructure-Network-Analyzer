# Network Analyzer

A console application that builds an undirected, unweighted social network graph from a tab-separated input file and provides analytics like friend counts, friend circles by college, closeness centrality, and connector students.

## Files
- [StudentNetworkAnalyzer.java](StudentNetworkAnalyzer.java): Main program and graph implementation (class Network_Analyzer).
- [input.txt](input.txt): Example input data file (TSV).

## Requirements
- Java JDK (11+ recommended).

## Build and Run
- Compile:

```bash
cd /workspaces/Student-Network-Analyzer
javac StudentNetworkAnalyzer.java
```

- Run:

```bash
java Network_Analyzer
```

When prompted, enter the path to your TSV input file (for example, `input.txt`).

## Input Format (TSV)
- Header line followed by one line per student.
- Columns:
	1. `id` (long)
	2. `firstName`
	3. `lastName`
	4. `college`
	5. `department`
	6. `email`
	7. `friendCount` (int)
	8. `friendId1` ... `friendIdN` (exactly `friendCount` ids)

Example (tabs shown as `\t` for clarity):

```
id	first	last	college	department	email	friendCount	friendId1	friendId2
1	Alice	Smith	"Engineering"	ECE	alice@osu.edu	2	2	3
2	Bob	Jones	"Engineering"	ME	bob@osu.edu	1	1
3	Carol	Lee	"Arts & Sciences"	Math	carol@osu.edu	1	1
```

Notes:
- `college` values may be quoted in the input; quotes are handled.
- Friendships are treated as undirected and added only once.

## Features
- Remove friendship: deletes an undirected edge between two students.
- Delete account: removes a student and all incident friendships.
- Count friends: shows count and lists direct neighbors.
- Friends circle: lists connected groups filtered by `college`.
- Closeness centrality: computes and shows raw and normalized scores.
- Find connectors: identifies articulation points in the graph.

## Usage Tips
- Keep ids unique across the file.
- Ensure `friendCount` matches the number of following friend ids on the line.
- Use the menu to perform operations after the file loads.
