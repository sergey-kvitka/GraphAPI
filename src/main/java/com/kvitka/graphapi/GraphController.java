package com.kvitka.graphapi;

import com.kvitka.graphapi.graph.Edge;
import com.kvitka.graphapi.graph.MathGraph;
import com.kvitka.graphapi.graph.Vertex;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("graph")
public final class GraphController {

    private MathGraph mathGraph;
    private Integer vertexName = 1;

    public GraphController() {
        mathGraph = new MathGraph();
    }

    private void updateVertexName() {
        vertexName = mathGraph.getMaxVertexIntName() + 1;
    }

    private String formatStringFromJSON(String filepath) {
        return filepath.replace("\"", "")
                .replace("\\\\", "\\");
    }

    @GetMapping("get_vertices")
    public List<Vertex> getVertices() {
        return mathGraph.getVertices();
    }

    @GetMapping("get_edges")
    public List<Edge> getEdges() {
        return mathGraph.getEdges();
    }

    @PostMapping("add_vertex")
    public void addVertex() {
        mathGraph.addVertex(new Vertex(vertexName.toString()), vertexName.toString());
        updateVertexName();
    }

    @PostMapping("add_edge/{v_name1}/{v_name2}")
    public void addEdge(@PathVariable(value = "v_name1") String v1_path,
                        @PathVariable(value = "v_name2") String v2_path,
                        @RequestBody String weight_) {
        if ("".equals(weight_)) weight_ = "1";
        double weight = Double.parseDouble(weight_);
        if (weight == 0) return;
        Vertex v1 = mathGraph.getVertex(v1_path);
        Vertex v2 = mathGraph.getVertex(v2_path);

        if ((v1 != null && v2 != null) && mathGraph.getEdge(v1, v2) == null) {
            mathGraph.addEdge(new Edge(v1, v2, weight));
        }
    }

    @DeleteMapping("delete_last_vertex")
    public void deleteLastVertex() {
        int verticesSize = mathGraph.getVertices().size();
        if (verticesSize > 0) {
            mathGraph.deleteVertex(verticesSize - 1);
        }
        updateVertexName();
    }

    @DeleteMapping("delete_vertex/{name}")
    public void deleteVertex(@PathVariable(value = "name") String name) {
        if (mathGraph.containsVertexName(name)) {
            mathGraph.deleteVertex(name);
            updateVertexName();
        }
    }

    @DeleteMapping("delete_edge/{v1_name}/{v2_name}")
    public void deleteEdge(@PathVariable String v1_name, @PathVariable String v2_name) {
        Vertex v1 = mathGraph.getVertex(v1_name);
        Vertex v2 = mathGraph.getVertex(v2_name);
        mathGraph.deleteEdge(new Edge(v1, v2));
    }

    @DeleteMapping("clear_graph")
    public void clearGraph() {
        mathGraph.clear();
        updateVertexName();
    }

    @PostMapping("complete_graph")
    public void completeGraph() {
        mathGraph.toCompleteGraph();
    }

    @DeleteMapping("delete_all_edges")
    public void deleteAllEdges() {
        mathGraph.clearEdges();
    }

    @PostMapping("create_graph_by_adjacency_matrix")
    public void loadGraphFromFileWithMatrix(@RequestBody String filepath) {
        mathGraph = MathGraph.createGraphByAdjacencyMatrix(formatStringFromJSON(filepath));
        updateVertexName();
    }

    @PostMapping("create_graph_by_edge_list")
    public void loadGraphFromFileWithEdgeList(@RequestBody String filepath) {
        mathGraph = MathGraph.createGraphByEdgesList(formatStringFromJSON(filepath));
        if (mathGraph.getVerticesAmount() == 0) return;
        updateVertexName();
    }

    @PostMapping("write_adjacency_matrix_into_file")
    public void writeAdjacencyMatrixIntoFile(@RequestBody String filepath) {
        mathGraph.writeAdjacencyMatrixIntoFile(formatStringFromJSON(filepath));
    }

    @PostMapping("write_edge_list_into_file")
    public void writeEdgeListIntoFile(@RequestBody String filepath) {
        mathGraph.writeEdgeListIntoFile(formatStringFromJSON(filepath));
    }

    @GetMapping("has_edge_with_vertices_with_3_adjacent")
    public List<Edge> hasEdgeWithVerticesWith3AdjacentVertices() {
        System.out.println(mathGraph.hasEdgeWithVerticesWith3AdjacentVertices());
        return mathGraph.hasEdgeWithVerticesWith3AdjacentVertices();
    }

    @PostMapping("set_edge_weight/{v1_name}/{v2_name}")
    public void setEdgeWeight(@PathVariable String v1_name,
                              @PathVariable String v2_name,
                              @RequestBody String new_weight) {
        double weight;
        try {
            weight = Double.parseDouble(formatStringFromJSON(new_weight));
        } catch (NumberFormatException e) {
            return;
        }

        List<String> names = mathGraph.getVertexUniqueNames();
        if (!names.contains(v1_name) || !names.contains(v2_name)) return;
        List<Vertex> vertices = mathGraph.getVertices();
        Vertex v1 = vertices.get(names.indexOf(v1_name));
        Vertex v2 = vertices.get(names.indexOf(v2_name));
        mathGraph.setEdgeWeight(v1, v2, weight);
    }
}
