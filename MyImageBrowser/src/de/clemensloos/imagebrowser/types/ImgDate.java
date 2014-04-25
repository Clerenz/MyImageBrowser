package de.clemensloos.imagebrowser.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class ImgDate implements Comparable<ImgDate>, MutableTreeNode{

	public int year;
	public int month;
	public int day;
	
	public long longDateStart;
	public long longDateEnd;

	int numImages;

	private static SimpleDateFormat sdf_day = new SimpleDateFormat("y-MM-dd");
	private static long oneDay = 86400000l;
	
	public ImgDate(String date, int numImages) {
	
		String[] tmp = date.split("-");
		try{
			year = Integer.parseInt(tmp[0]);
			month = Integer.parseInt(tmp[1]);
			day = Integer.parseInt(tmp[2]);
		} catch ( NumberFormatException e ) {
			// TODO
			e.printStackTrace();
		}
		
		this.numImages = numImages;
		
		try {
			longDateStart = sdf_day.parse(date).getTime();
			longDateEnd = sdf_day.parse(date).getTime() + oneDay;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getYear() {
		return "" + year;
	}
	
	public String getMonth() {
		switch(month) {
		case(1): return "Januar";
		case(2): return "Februar";
		case(3): return "März";
		case(4): return "April";
		case(5): return "Mai";
		case(6): return "Juni";
		case(7): return "Juli";
		case(8): return "August";
		case(9): return "September";
		case(10): return "Oktober";
		case(11): return "November";
		case(12): return "Dezember";
		default: return "";
		}
	}
	
	public String getDay() {
		String theDay = "" + day;
		if(day < 10) {
			theDay = "0" + theDay;
		}
		String theMonth = "" + month;
		if(month < 10) {
			theMonth = "0" + theMonth;
		}
		return theDay+"."+theMonth+"."+year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ImgDate))
			return false;
		ImgDate other = (ImgDate) obj;
		if (day != other.day)
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public int compareTo(ImgDate o) {
		if(this.year < o.year){
			return -1;
		}
		else if(this.year > o.year) {
			return 1;
		}
		else if(this.month < o.month) {
			return -1;
		}
		else if(this.month > o.month) {
			return 1;
		}
		else if (this.day < o.day) {
			return -1;
		}
		else if(this.day > o.day) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return getDay() + " (" + numImages + ")";
	}

	@Override
	public Enumeration<?> children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
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
		return true;
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
