from graphviz import Source

# Read the Graphviz graph from a file
with open('module_dependency_graph.dot', 'r') as file:
    graph = file.read()

# Create a Source object
src = Source(graph)

# Render the graph to an SVG file
src.render(filename='module_dependency_graph', format='svg', cleanup=True)