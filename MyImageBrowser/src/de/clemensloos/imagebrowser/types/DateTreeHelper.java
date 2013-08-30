package de.clemensloos.imagebrowser.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class DateTreeHelper implements MutableTreeNode{

	private String name;
	
	List<Object> children;
	
	public DateTreeHelper(String name) {
		this.name = name;
		children = new ArrayList<Object>();
	}
	
	public void add(DateTreeHelper child) {
		children.add(child);
	}
	
	public void add(Date date) {
		String year = date.getYear();
		for(Object o : children) {
			if(o instanceof DateTreeHelper) {
				if(o.toString().equals(year)) {
					((DateTreeHelper)o).addSecond(date);
					return;
				}
			}
		}
		DateTreeHelper dth = new DateTreeHelper(year);
		add(dth);
		dth.addSecond(date);
	}
	
	public void addSecond(Date date) {
		String month = date.getMonth();
		for(Object o : children) {
			if(o instanceof DateTreeHelper) {
				if(o.toString().equals(month)) {
					((DateTreeHelper)o).children.add(date);
					return;
				}
			}
		}
		DateTreeHelper dth = new DateTreeHelper(month);
		add(dth);
		dth.children.add(date);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Enumeration children() {
		return Collections.enumeration(children);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
	}

	@Override
	public void remove(int index) {
	}

	@Override
	public void remove(MutableTreeNode node) {
	}

	@Override
	public void removeFromParent() {
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
	}

	@Override
	public void setUserObject(Object object) {
	}

	
}
