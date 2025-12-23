"""
Generate an interactive visualization of the infrastructure network.

This script reads a TSV-formatted file called ``input.txt`` containing a list
of infrastructure nodes and their undirected connections. It computes
articulation points (connectors) using a depth‑first search algorithm and
produces a self-contained HTML file (``infrastructure_graph.html``) that
visualizes the network using the vis.js library. Connectors are highlighted
with a distinct color and labeled as critical to make them easy to spot.

The generated HTML file requires internet access to fetch the vis-network
library from a CDN. Open ``infrastructure_graph.html`` in a modern web browser
to explore the graph. Nodes can be dragged, zoomed, and panned.
"""

import csv
import json
from pathlib import Path


def parse_input(filename: str):
    """Parse the TSV input file and return node and adjacency information.

    Returns a tuple (nodes, adj) where ``nodes`` is a list of dictionaries
    containing node properties and ``adj`` is a dictionary mapping node IDs
    to sets of neighbor IDs.
    """
    nodes = []
    adj = {}
    with open(filename, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f, delimiter='\t')
        for row in reader:
            node_id = int(row['id'])
            nodes.append({
                'id': node_id,
                'name': row['nodeName'],
                'group': row['group'],
                'unit': row['unit'],
                'contact': row['contact'],
            })
            adj.setdefault(node_id, set())
            # Gather up to five connection IDs; ignore missing columns
            for i in range(1, 6):
                key = f'connectionID{i}'
                if key in row and row[key] and row[key].strip():
                    try:
                        target = int(row[key])
                        if target != node_id:
                            adj[node_id].add(target)
                    except ValueError:
                        pass
    # Since the graph is undirected, ensure adjacency is symmetric
    for u, neighbors in list(adj.items()):
        for v in neighbors:
            adj.setdefault(v, set()).add(u)
    return nodes, adj


def find_articulation_points(adj):
    """Return a set of articulation points in the undirected graph.

    ``adj`` is a mapping from node ID to a set of neighbor IDs.
    This implementation follows the classical DFS algorithm that computes
    discovery times and low values.
    """
    time = 0
    visited = set()
    disc = {}
    low = {}
    parent = {}
    articulation = set()

    def dfs(u):
        nonlocal time
        visited.add(u)
        disc[u] = time
        low[u] = time
        time += 1
        children = 0
        for v in adj.get(u, []):
            if v not in visited:
                parent[v] = u
                children += 1
                dfs(v)
                low[u] = min(low[u], low[v])
                # (1) If u is root and has more than one child, it's an articulation point
                if parent.get(u) is None and children > 1:
                    articulation.add(u)
                # (2) If u is not root and low value of one child is no less than discovery of u
                if parent.get(u) is not None and low[v] >= disc[u]:
                    articulation.add(u)
            elif parent.get(u) != v:
                # Back edge
                low[u] = min(low[u], disc[v])

    for u in adj:
        if u not in visited:
            dfs(u)
    return articulation


def build_html(nodes, adj, connectors, output_path: Path):
    """Generate an HTML file that visualizes the graph using vis.js.

    ``nodes`` is a list of node dictionaries with properties; ``adj`` is a
    dictionary mapping node IDs to neighbor sets; ``connectors`` is a set of
    articulation point IDs; ``output_path`` is the path where the HTML file
    should be written.
    """
    # Define colors for each group. Unknown groups fall back to a default.
    group_colors = {
        'Engineering': '#3498db',
        'Research': '#e74c3c',
        'Operations': '#2ecc71',
        'Support': '#9b59b6',
    }
    default_color = '#95a5a6'

    vis_nodes = []
    for node in nodes:
        node_id = node['id']
        group = node['group']
        name = node['name']
        unit = node['unit']
        contact = node['contact']
        if node_id in connectors:
            color = '#f1c40f'  # highlight connectors in yellow/gold
            label = f"CRITICAL: {name}"
            border_width = 3
        else:
            color = group_colors.get(group, default_color)
            label = name
            border_width = 1
        vis_nodes.append({
            'id': node_id,
            'label': label,
            'title': f"Unit: {unit}\nContact: {contact}",
            'color': color,
            'borderWidth': border_width,
        })

    # Build edge list, ensuring each undirected edge is added once
    added = set()
    vis_edges = []
    for u, neighbors in adj.items():
        for v in neighbors:
            key = frozenset((u, v))
            if key not in added:
                added.add(key)
                vis_edges.append({'from': u, 'to': v})

    # Construct the HTML content
    html_content = f"""
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Infrastructure Network Visualization</title>
  <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
  <style type="text/css">
    body {{ background-color: #222222; color: #ffffff; margin: 0; padding: 0; }}
    #network {{ width: 100%; height: 750px; }}
  </style>
</head>
<body>
  <div id="network"></div>
  <script type="text/javascript">
    // Data for nodes and edges
    var nodes = new vis.DataSet({json.dumps(vis_nodes)});
    var edges = new vis.DataSet({json.dumps(vis_edges)});
    var container = document.getElementById('network');
    var data = {{ nodes: nodes, edges: edges }};
    var options = {{
      nodes: {{
        shape: 'dot',
        font: {{ color: '#ffffff' }},
        size: 16
      }},
      edges: {{ color: '#aaaaaa', smooth: false }},
      layout: {{ improvedLayout: true }},
      physics: {{
        enabled: true,
        forceAtlas2Based: {{
          gravitationalConstant: -50,
          centralGravity: 0.01,
          springLength: 100,
          springConstant: 0.08
        }},
        solver: 'forceAtlas2Based',
        timestep: 0.5,
        stabilization: {{ iterations: 100 }}
      }},
      interaction: {{ hover: true, tooltipDelay: 200 }}
    }};
    var network = new vis.Network(container, data, options);
  </script>
</body>
</html>
"""
    # Write the HTML to file
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(html_content)


def main():
    # Parse nodes and adjacency from the input file
    input_file = 'input.txt'
    nodes, adj = parse_input(input_file)

    # Compute articulation points (connectors)
    connectors = find_articulation_points(adj)

    # Generate the HTML visualization
    output_path = Path('infrastructure_graph.html')
    build_html(nodes, adj, connectors, output_path)
    print(f"Visualization written to {output_path}")


if __name__ == '__main__':
    main()