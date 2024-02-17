import os
from pathlib import Path
import re
from graphviz import Digraph

# Root directory of the Android project i.e Current Directory
root_dir = os.getcwd()

# Find all build.gradle files in the project directory and its subdirectories
build_files = [str(f) for f in Path(root_dir).rglob("build.gradle.kts")]

# Create a dictionary to store the dependencies graph
dependencies_graph = {}

# Define a function to extract dependencies from a build.gradle file
def extract_dependencies(file_path):
    with open(file_path, 'r') as file:
        content = file.read()
        dependencies_block_match = re.search(r'dependencies\s*{([^}]*)}', content)
        if dependencies_block_match:
            dependencies_block = dependencies_block_match.group(1)
            dependencies = re.findall(r'\(([^)]*)\)', dependencies_block)
            return [dep.replace("projects.", "") for dep in dependencies if dep.startswith("projects.")]
        else:
            return []

# Populate the dependencies graph
for build_file in build_files:
    module = os.path.dirname(build_file).replace(root_dir, '').lstrip('/').replace('/', '.')
    dependencies_graph[module] = extract_dependencies(build_file)

# Create a Digraph object
dot = Digraph(format='svg')
dot.attr('node', shape='box3d', style='filled', fillcolor='lightblue')
dot.attr('graph', splines='polyline')

# Add nodes and edges to the graph
for module, deps in dependencies_graph.items():
    for dep in deps:
        dot.edge(module, dep)

# Save the graph to a SVG file
dot.render('dependency_graph')
