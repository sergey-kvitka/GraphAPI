package com.kvitka.graphapi.graph;

import java.util.Objects;

public class Vertex {
    private static int staticId = 0;

    public final int id;
    public String name;

    public Vertex() {
        id = Vertex.staticId++;
    }

    public Vertex(String name) {
        this.name = name;
        id = Vertex.staticId++;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        return this.id == ((Vertex) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
