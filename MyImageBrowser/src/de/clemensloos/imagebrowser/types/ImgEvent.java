package de.clemensloos.imagebrowser.types;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class ImgEvent implements Comparable<ImgEvent>, TreeNode {

	public String eventname;
	
	public long eventstart;
	public long eventend;
	
	public ImgEvent(String eventname, long eventstart, long eventend) {
		this.eventname = eventname;
		
		this.eventstart = eventstart;
		this.eventend = eventend;
	}
	
	@Override
	public String toString() {
		return this.eventname;
	}
	


	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventname == null) ? 0 : eventname.hashCode());
		result = prime * result + (int) (eventstart ^ (eventstart >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ImgEvent)) {
			return false;
		}
		ImgEvent other = (ImgEvent) obj;
		if (eventname == null) {
			if (other.eventname != null) {
				return false;
			}
		} else if (!eventname.equals(other.eventname)) {
			return false;
		}
		if (eventstart != other.eventstart) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(ImgEvent arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Enumeration<?> children() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndex(TreeNode arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

}
