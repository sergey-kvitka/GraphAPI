package com.kvitka.graphapi.graph;

import org.jetbrains.annotations.NotNull;

public class Edge {
    private final Vertex v1;
    private final Vertex v2;

    private String name;
    private double weight;

    public Edge(@NotNull Vertex v1, @NotNull Vertex v2) {
        this(v1, v2, 1, "");
    }

    public Edge(@NotNull Vertex v1, @NotNull Vertex v2, double weight) {
        this(v1, v2, weight, "");
    }

    public Edge(@NotNull Vertex v1, @NotNull Vertex v2, String name) {
        this(v1, v2, 1, name);
    }

    public Edge(@NotNull Vertex v1, @NotNull Vertex v2,
                double weight, String name) {
        if (weight == 0) throw new IllegalArgumentException("Edge's weight can't be zero");
        if (v1.id == v2.id) throw new IllegalArgumentException("Looped edges are not supported");
        this.name = name;
        this.weight = weight;

        if (v1.id < v2.id) {
            this.v1 = v1;
            this.v2 = v2;
        }
        else {
            this.v2 = v1;
            this.v1 = v2;
        }
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight)  {
        setWeight((double) weight);
    }

    public void setWeight(double weight)  {
        if (weight == 0) throw new IllegalArgumentException("Edge's weight can't be zero");
        this.weight = weight;
    }

    public boolean v1EqualsTo(Vertex vertex) {
        return vertex.equals(v1);
    }

    public boolean v2EqualsTo(Vertex vertex) {
        return vertex.equals(v2);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                ", weight=" + weight +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Edge that = (Edge) o;
        return v1EqualsTo(that.getV1()) && v2EqualsTo(that.getV2());
    }
}
