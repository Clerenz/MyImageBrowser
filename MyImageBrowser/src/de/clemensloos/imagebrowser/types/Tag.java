package de.clemensloos.imagebrowser.types;


public class Tag implements Comparable<Tag>{

	public String tagname;


	public Tag(String tagname) {
		this.tagname = tagname;
	}
	
	
	@Override
	public String toString() {
		return this.tagname;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tagname == null) ? 0 : tagname.toLowerCase().hashCode());
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
		if (!(obj instanceof Tag)) {
			return false;
		}
		Tag other = (Tag) obj;
		if (tagname == null) {
			if (other.tagname != null) {
				return false;
			}
		} else if (!tagname.equalsIgnoreCase(other.tagname)) {
			return false;
		}
		return true;
	}


	@Override
	public int compareTo(Tag other) {
		return this.tagname.compareToIgnoreCase(other.tagname);
	}

}
