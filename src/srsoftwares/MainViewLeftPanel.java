package srsoftwares;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: Apr 13, 2011
 * Time: 4:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainViewLeftPanel extends JPanel {
    private DefaultMutableTreeNode root;
    private JTree tree;
    private String instanceName;
    private DefaultMutableTreeNode instanceNode;

    MainViewLeftPanel(String instanceName, String databaseName) {
        this.instanceName = instanceName;
        root = new DefaultMutableTreeNode("");
        tree = new JTree(root);
        instanceNode = new DefaultMutableTreeNode(instanceName);

        List<String> SchemaNames = DataBaseUtility.getSchemaNames();
        for (String schemaName : SchemaNames) {
            DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(schemaName);
            List<String> tableNames = DataBaseUtility.getTableNames(schemaName);
            for (String tableName : tableNames) {
                DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
                schemaNode.add(tableNode);
            }

            instanceNode.add(schemaNode);
            root.add(instanceNode);
            tree.setSelectionPath(new TreePath(root.getPath()));
        }

        JScrollPane scrollPane = new JScrollPane(tree);
        tree.setCellRenderer(new TreeRenderer()); // Customized Tree Renderer
        this.setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

    }

    public JTree getTree() {
        return tree;
    }

    public void refreshTreeView() {
        List<String> schemaNames = DataBaseUtility.getSchemaNames();
        for (String schemaName : schemaNames) {
            List<String> tableNames = DataBaseUtility.getTableNames(schemaName);

            DefaultMutableTreeNode schemaNode = getChildNode(instanceNode, schemaName);

            // checking whether a new node will be added
            if (schemaNode.getChildCount() != tableNames.size()) {
                // Adding Nodes to Tree which are presents in Database but not in Tree
                for (String tableName : tableNames) {
                    if (!isChildNodeExist(schemaNode, tableName)) {

                        schemaNode.add(new DefaultMutableTreeNode(tableName));
                    }
                }

                for(int i=0;i<schemaNode.getChildCount();i++){
                    DefaultMutableTreeNode tableNode= (DefaultMutableTreeNode) schemaNode.getChildAt(i);
                    String nodeName=tableNode.getUserObject().toString();
                    if(!tableNames.contains(nodeName)){
                        schemaNode.remove(i);
                    }
                }

            }
        //    tree.setSelectionPath(new TreePath(schemaNode.getParent()));
        }

        tree.updateUI();
        this.updateUI();
    }

    public boolean isChildNodeExist(DefaultMutableTreeNode parentNode, String keyNode) {

        for (int i = 0; i < parentNode.getChildCount(); i++) {
            if (((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject().toString().equals(keyNode)) {
                return true;
            } else
                continue;
        }

        return false;
    }

    public DefaultMutableTreeNode getChildNode(DefaultMutableTreeNode parentNode, String nodeName) {
        DefaultMutableTreeNode node = null;
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            node = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            if (node.getUserObject().toString().equals(nodeName))
                return node;
            else continue;

        }
        return node;
    }
}
