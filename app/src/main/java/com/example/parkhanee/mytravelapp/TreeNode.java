package com.example.parkhanee.mytravelapp;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 10. 7..
 */

public class TreeNode {
    String data;
    TreeNode parent;
    ArrayList<TreeNode> children;

    public void printLabels(TreeNode root){
        // 루트에 저장된 값을 출력한다
        System.out.println(root.data);

        //각 자손들을 루트로 하는 서브트리에 포함된 값들을 재귀적으로 출력한다
        for (int i=0; i< root.children.size(); i++){
            printLabels(root.children.get(i));
        }
    }

    public void ex(TreeNode d){
        printLabels(d);
    }

}




