package de.clemensloos.imagebrowser.types;

public class ImgPerson implements Comparable<ImgPerson> {

	public String username;
	
	public ImgPerson (String username) {
		this.username = username;
	}
	
	@Override
	public String toString() {
		return this.username;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.toLowerCase().hashCode());
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
		if (!(obj instanceof ImgPerson)) {
			return false;
		}
		ImgPerson other = (ImgPerson) obj;
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equalsIgnoreCase(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(ImgPerson other) {
		return this.username.compareToIgnoreCase(other.username);
	}

}
