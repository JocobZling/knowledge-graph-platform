package com.example.knowledgegraph.dto;

import java.util.ArrayList;
import java.util.List;

public class GraphData {
    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphLink> links = new ArrayList<>();

    public List<GraphNode> getNodes() { return nodes; }
    public void setNodes(List<GraphNode> nodes) { this.nodes = nodes; }
    public List<GraphLink> getLinks() { return links; }
    public void setLinks(List<GraphLink> links) { this.links = links; }

    public static class GraphNode {
        private String id;
        private String name;
        private String type;
        private String category;

        public GraphNode() {}
        public GraphNode(String id, String name, String type, String category) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.category = category;
        }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class GraphLink {
        private String source;
        private String target;
        private String name;

        public GraphLink() {}
        public GraphLink(String source, String target, String name) {
            this.source = source;
            this.target = target;
            this.name = name;
        }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
