package autolib_catalog;


import com.google.api.services.books.model.Volume;

public class LibraryBook implements Comparable<LibraryBook>{
	private final Volume volume;
	private boolean available;
	private int shelfNumber;
	
	public LibraryBook(Volume v, int s) {
		volume = v;
		available = true;
		shelfNumber = s;
	}
	
	public String getTitle(){
		return volume.getVolumeInfo().getTitle();
	}
	
	public int getShelfNumber(){
		return shelfNumber;
	}
	
	public void setShelfNumber(int i){
		shelfNumber = i;
	}
	
	public java.util.List<String> getAuthors(){
		return volume.getVolumeInfo().getAuthors();
	}
	
	public int getNumPages() {
		return volume.getVolumeInfo().getPageCount();
	}
	
	public String getDescription() {
		return volume.getVolumeInfo().getDescription();
	}
	
	public String getPublicationDate() {
		return volume.getVolumeInfo().getPublishedDate();
	}
	
	public boolean isAvailable() {
		return this.available;
	}
	
	public void setAvailable() {
		this.available = true;
	}
	
	public void setCheckedOut() {
		this.available = false;
	}

	public int compareTo(LibraryBook o) {
		return this.getTitle().compareTo(((LibraryBook)o).getTitle());
	}
}
