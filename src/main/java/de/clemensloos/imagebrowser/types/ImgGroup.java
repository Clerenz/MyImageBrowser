package de.clemensloos.imagebrowser.types;

public class ImgGroup implements Comparable<ImgGroup> {

	public String groupname;
	
	public ImgGroup (String username) {
		this.groupname = username;
	}
	
	@Override
	public String toString() {
		return this.groupname;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupname == null) ? 0 : groupname.toLowerCase().hashCode());
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
		if (!(obj instanceof ImgGroup)) {
			return false;
		}
		ImgGroup other = (ImgGroup) obj;
		if (groupname == null) {
			if (other.groupname != null) {
				return false;
			}
		} else if (!groupname.equalsIgnoreCase(other.groupname)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(ImgGroup other) {
		return this.groupname.compareToIgnoreCase(other.groupname);
	}

}
