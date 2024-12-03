package net.ximatai.muyun.test.util;

import net.ximatai.muyun.model.TreeNode;
import net.ximatai.muyun.util.TreeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TreeBuilderTest {

    static List list = List.of(
        buildNode("A", null),
        buildNode("B", null),
        buildNode("C", null),
        buildNode("A.a", "A"),
        buildNode("A.b", "A"),
        buildNode("A.a.1", "A.a"),
        buildNode("C.a.1", "C.a"),
        buildNode("B.a", "B")
    );

    @Test
    @DisplayName("测试构建整棵树且不显示根节点")
    void testTree() {
        List<TreeNode> tree = TreeBuilder.build("id", "pid", list, null, false, "name", 10);
        TreeNode nodeA = tree.get(0);
        assertEquals("A", nodeA.getLabel());
        assertEquals(2, nodeA.getChildren().size());
        assertEquals("A.a.1", nodeA.getChildren().get(0).getChildren().get(0).getLabel());
    }

    @Test
    @DisplayName("测试构建以'A'为根节点的树但显示根节点")
    void testTreeA() {
        List<TreeNode> tree = TreeBuilder.build("id", "pid", list, "A", true, "name", 10);
        TreeNode nodeA = tree.get(0);
        assertEquals("A", nodeA.getLabel());
        assertEquals(2, nodeA.getChildren().size());
        assertEquals("A.a.1", nodeA.getChildren().get(0).getChildren().get(0).getLabel());
    }

    @Test
    @DisplayName("测试构建以'A'为根节点且不显示根节点")
    void testTreeANotShowMe() {
        List<TreeNode> tree = TreeBuilder.build("id", "pid", list, "A", false, "name", 10);
        assertEquals(2, tree.size());
        assertEquals("A.a.1", tree.get(0).getChildren().get(0).getLabel());
    }

    @Test
    @DisplayName("测试构建不存在的根节点")
    void testTreeNotFound() {
        List<TreeNode> tree = TreeBuilder.build("id", "pid", list, "X", false, "name", 10);
        assertNull(tree);
    }

    static Map buildNode(String id, String pid) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("pid", pid);
        map.put("name", id);
        return map;
    }
}
