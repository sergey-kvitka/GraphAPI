package com.kvitka.graphapi.graph;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MathGraph {

    private final List<Edge> edges = new ArrayList<>();
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<String> vertexUniqueNames = new ArrayList<>();
    private int currentVertexNameNumber = 1;

    public MathGraph() {}

    public MathGraph(int vertexNameNumberStart) { currentVertexNameNumber = vertexNameNumberStart; }

    public List<Vertex> getVertices() {
        return new ArrayList<>(vertices);
    }

    public String getLastUniqueName() {
        if (vertexUniqueNames.size() == 0) return "0";
        return vertexUniqueNames.get(vertexUniqueNames.size() - 1);
    }

    public int getMaxVertexIntName() {
        try {
            return vertexUniqueNames.stream().mapToInt(Integer::parseInt).max().orElse(0);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Map<String, Vertex> getVerticesWithNames() {
        Map<String, Vertex> result = new HashMap<>();
        int size = vertices.size();
        for (int i = 0; i < size; i++) {
            result.put(vertexUniqueNames.get(i), vertices.get(i));
        }
        return result;
    }

    public List<String> getVertexUniqueNames() {
        return new ArrayList<>(vertexUniqueNames);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public void addVertex(Vertex vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            while (vertexUniqueNames.contains("v" + currentVertexNameNumber)) { currentVertexNameNumber++; }
            vertexUniqueNames.add("v" + currentVertexNameNumber++);
        }
    }

    public void addVertex(Vertex vertex, String vertexName) {
        if (vertexName == null) {
            addVertex(vertex);
            return;
        }
        if (!vertexUniqueNames.contains(vertexName)) {
            vertices.add(vertex);
            vertexUniqueNames.add(vertexName);
        }
        addVertex(vertex);
    }

    public void addVertices(Vertex...vertices) {
        for (Vertex v : vertices) {
            addVertex(v);
        }
    }

    public void addEdge(Edge edge) {
        if (edges.contains(edge)) return;
        addVertex(edge.getV1());
        addVertex(edge.getV2());
        edges.add(edge);
    }

    public void addEdges(Edge...edges) {
        for (Edge e : edges) {
            addEdge(e);
        }
    }

    public void deleteEdge(int index) {
        edges.remove(index);
    }

    public void deleteEdge(Edge edge) {
        for (int i = edges.size() - 1; i >= 0; i--) {
            if (edges.get(i).equals(edge)) {
                edges.remove(i);
            }
        }
    }

    public void deleteVertex(int index) {
        Vertex vertexToDelete = vertices.get(index);

        List<Edge> filteredEdges = edges.stream()
                .filter(edge -> (!edge.getV1().equals(vertexToDelete) && !edge.getV2().equals(vertexToDelete)))
                .collect(Collectors.toList());

        edges.clear(); edges.addAll(filteredEdges);
        vertices.remove(index);
        vertexUniqueNames.remove(index);
    }

    public void deleteVertex(Vertex vertex) {
        if (vertices.contains(vertex))
            deleteVertex(vertices.indexOf(vertex));
    }

    public void deleteVertex(String vertexName) {
        if (vertexUniqueNames.contains(vertexName)) {
            deleteVertex(vertexUniqueNames.indexOf(vertexName));
        }
    }

    public void renameVertex(int index, String name) {
        if (!vertexUniqueNames.contains(name)) {
            vertexUniqueNames.set(index, name);
        }
    }

    public void renameVertex(Vertex vertex, String name) {
        if (vertices.contains(vertex)) {
            renameVertex(vertices.indexOf(vertex), name);
        }
    }

    public boolean containsVertexName(String name) {
        return vertexUniqueNames.contains(name);
    }

    public Vertex getVertex(int index) {
        return vertices.get(index);
    }

    public Vertex getVertex(String vertexName) {
        if (vertexUniqueNames.contains(vertexName))
            return vertices.get(vertexUniqueNames.indexOf(vertexName));
        return null;
    }

    public Edge getEdge(int index) {
        return edges.get(index);
    }

    public Edge getEdge(Vertex v1, Vertex v2) { //доп к 7, 10
        if (v1.id > v2.id) {
            Vertex v = v1; v1 = v2; v2 = v;
        }
        for (Edge edge : edges) {
            if (edge.getV1() == v1 && edge.getV2() == v2) {
                return edge;
            }
        }
        return null;
    }

    public void setEdgeWeight(Vertex v1, Vertex v2, double weight) {
        edges.get(edges.indexOf(new Edge(v1, v2))).setWeight(weight);
    }

    public ArrayList<Vertex> getAdjacentVertices(Vertex vertex) {
        ArrayList<Vertex> neighbors = new ArrayList<>();
        neighbors.add(vertex);
        for (Edge edge : edges) {
            if (edge.v1EqualsTo(vertex) && !neighbors.contains(edge.getV2())) {
                neighbors.add(edge.getV2());
            }
            else if (edge.v2EqualsTo(vertex) && !neighbors.contains(edge.getV1())) {
                neighbors.add(edge.getV1());
            }
        }
        neighbors.remove(0);
        return neighbors;
    }

    public int getAdjacentVerticesAmount(Vertex vertex) {
        return getAdjacentVertices(vertex).size();
    }

    public int getVerticesAmount() {
        return vertices.size();
    }

    public int getEdgesAmount() {
        return edges.size();
    }

    public boolean areAdjacent(Vertex v1, Vertex v2) {
        return (getEdge(v1, v2) != null);
    }

    public double getWeightOfEdgeByVertices(Vertex v1, Vertex v2) {
        Edge edge = getEdge(v1, v2);
        if (edge == null) throw new IllegalArgumentException(
                "There are no edge in this graph with such vertices");
        return edge.getWeight();
    }

    public void clearEdges() {
        edges.clear();
    }

    public void toCompleteGraph() {
        int vertexAmount = vertices.size();
        for (int i = 0; i < vertexAmount; i++) {
            for (int j = i + 1; j < vertexAmount; j++) {
                addEdge(new Edge(vertices.get(i), vertices.get(j)));
            }
        }
    }

    public void clear() {
        edges.clear(); vertices.clear(); vertexUniqueNames.clear();
        currentVertexNameNumber = 1;
    }

    @Override
    public String toString() {
        StringBuilder edgesStr = new StringBuilder();

        int size = edges.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) edgesStr.append(",\n");
            edgesStr.append("\t").append(edges.get(i));
        }
        return "MathGraph{" +
                "\nvertices=" + getVerticesWithNames() + (
                edgesStr.length() == 0
                        ? ",\nno edges"
                        :  (",\nedges=\n" + edgesStr + "\n}")
        );
    }

    public static MathGraph createGraphByEdgesList(String filePath) {
        MathGraph graph = new MathGraph();
        Map<String, Vertex> newVertices = new HashMap<>();
        String line;
        boolean firstString = true;
        List<String> data = new ArrayList<>();
        String[] currentVertexNames = new String[2];

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (firstString) { firstString = false; continue; }
                line = line.trim();
                if (line.length() == 0) continue;
                while(line.contains("  ")) {
                    line = line.replace("  ", " "); // 2 '_' --> 1 '_'
                }
                data.addAll(Arrays.asList(line.split(" ")));
                if (data.size() < 3) throw new IllegalArgumentException("Wrong data format in file");

                for (int i = 0; i < 2; i++) {
                    currentVertexNames[i] = data.get(i);
                    if (!newVertices.containsKey(data.get(i))) {
                        Vertex v = new Vertex(data.get(i));
                        newVertices.put(data.get(i), v);
                        graph.addVertex(v, data.get(i));
                    }
                }
                graph.addEdge(new Edge(
                        newVertices.get(currentVertexNames[0]),
                        newVertices.get(currentVertexNames[1]),
                        Double.parseDouble(data.get(2))
                ));
                data.clear();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong (edges list)");
            return new MathGraph();
        }
        return graph;
    }

    public static MathGraph createGraphByAdjacencyMatrix(String filePath) {
        MathGraph graph = new MathGraph();
        double[][] data = new double[0][0];
        String[] oneLineData;
        String line;
        boolean firstString = true;
        int i = 0;

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            while (bufferedReader.ready()) {
                int number = 0;
                if (firstString) {
                    number = Integer.parseInt(bufferedReader.readLine().trim());
                    data = new double[number][number];
                    firstString = false;
                    continue;
                }
                line = bufferedReader.readLine().trim();
                if (line.equals("")) continue;
                while(line.contains("  ")) {
                    line = line.replace("  ", " "); // 2 '_' --> 1 '_'
                }
                oneLineData = line.split(" ", number);
                for (int j = 0; j < oneLineData.length; j++) {
                    data[i][j] = Double.parseDouble(oneLineData[j]);
                }
                i++;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong (adjacency matrix)");
            return new MathGraph();
        }

        Map<Integer, Vertex> vertices = new HashMap<>();
        int dataLength = data.length;

        for (i = 0; i < dataLength; i++) {
            Vertex newVertex = new Vertex("" + (i + 1));
            vertices.put(i, newVertex);
            graph.addVertex(newVertex, "" + (i + 1));
        }

        for (i = 0; i < dataLength; i++) {
            for (int j = 0; j <= i; j++) {
                if (data[i][j] != data[j][i]) return new MathGraph();
                if (data[i][j] != 0) {
                    if (i == j) continue;
                    graph.addEdge(new Edge(vertices.get(i), vertices.get(j), data[i][j]));
                }
            }
        }
        return graph;
    }

    public void writeEdgeListIntoFile(String filepath) {
        try (FileWriter fileWriter = new FileWriter(filepath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            bufferedWriter.write("" + getEdgesAmount());
            bufferedWriter.newLine();
            for (Edge edge: edges) {
                String v1 = vertexUniqueNames.get(vertices.indexOf(edge.getV1()));
                String v2 = vertexUniqueNames.get(vertices.indexOf(edge.getV2()));
                bufferedWriter.write(v1 + " " + v2 + " " + edge.getWeight());
                bufferedWriter.newLine();
            }
        }
        catch (IOException e) {
            System.out.println("Something went wrong (writing edge list)");
        }
    }

    public void writeAdjacencyMatrixIntoFile(String filepath) {
        try (FileWriter fileWriter = new FileWriter(filepath, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            int verticesAmount = vertices.size();
            double[][] data = new double[verticesAmount][verticesAmount];
            bufferedWriter.write("" + verticesAmount);
            bufferedWriter.newLine();
            Vertex v1, v2; Edge edge; double weight;

            for (int i = 0; i < verticesAmount; i++) {
                for (int j = 0; j < i; j++) {
                    v1 = vertices.get(i);
                    v2 = vertices.get(j);
                    edge = getEdge(v1, v2);
                    if (edge == null) weight = 0;
                    else weight = edge.getWeight();
                    data[i][j] = weight;
                    data[j][i] = weight;
                }
                data[i][i] = 0;
            }
            StringBuilder row;
            for (int i = 0; i < verticesAmount; i++) {
                row = new StringBuilder();
                for (int j = 0; j < verticesAmount; j++) {
                    row.append(j == 0 ? "" : " ").append(data[i][j]);
                }
                bufferedWriter.write("" + row);
                bufferedWriter.newLine();
            }
        }
        catch (IOException e) {
            System.out.println("Something went wrong (writing adjacency matrix)");
        }
    }

    public List<Edge> hasEdgeWithVerticesWith3AdjacentVertices() {
        List<Edge> edgesWithRequireVertices = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (getAdjacentVerticesAmount(edge.getV1()) >= 3 &&
            getAdjacentVerticesAmount(edge.getV2()) >= 3) {
                edgesWithRequireVertices.add(edge);
            }
        }
        return edgesWithRequireVertices;
    }
}
