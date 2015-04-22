package autolib_tests;

import autolib_catalog.*;

import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volume.VolumeInfo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LibraryBookTest {

	private VolumeInfo volInfo;
	private Volume vol;

	@Before
	public void setUp() throws Exception {
		vol = new Volume();
		volInfo = new VolumeInfo();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorNullVolume() {
		new LibraryBook(null,1);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorEmptyVolumeInfo() {
		vol.setVolumeInfo(volInfo);
		new LibraryBook(vol,1);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorValidVolumeInvalidShelfZero() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		new LibraryBook(vol,0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorValidVolumeInvalidShelfNegative() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		new LibraryBook(vol,-1);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorValidVolumeInvalidShelfTooLarge() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		new LibraryBook(vol,20);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorEmptyVolume() {
		new LibraryBook(new Volume(),1);
	}
	
	@Test
	public void testConstructorValidVolumeAndShelf() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertNotNull("Valid LibraryBook() arguments produced null LibraryBook instance",book);
	}
	
	@Test
	public void testGetTitle() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertEquals("getTitle returned incorrect volume name with valid VolumeInfo and "
				+ "underlying Volume instance",book.getTitle(),"Moby Dick");
	}

	@Test
	public void testGetShelfNumber() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertEquals("getShelfNumber returned incorrect shelf",book.getShelfNumber(),1);
	}

	@Test
	public void testSetShelfNumberValid() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertEquals("incorrect shelf set by constructor",book.getShelfNumber(),1);
		book.setShelfNumber(2);
		assertEquals("valid set shelf test didn't set the correct shelf",book.getShelfNumber(),2);
	}
	
	@Test  (expected = IllegalArgumentException.class)
	public void testSetShelfNumberNegative() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertEquals("incorrect shelf set by constructor",book.getShelfNumber(),1);
		book.setShelfNumber(-1);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testSetShelfNumberZero() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertEquals("incorrect shelf set by constructor",book.getShelfNumber(),1);
		book.setShelfNumber(0);
	}
	
	@Test  (expected = IllegalArgumentException.class)
	public void testSetShelfNumberTooLarge() {
		volInfo.setTitle("Moby Dick");
		vol.setVolumeInfo(volInfo);
		LibraryBook book = new LibraryBook(vol,1);
		assertEquals("incorrect shelf set by constructor",book.getShelfNumber(),1);
		book.setShelfNumber(20);
	}

	@Test
	public void testGetAuthors() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumPages() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPublicationDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsAvailable() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAvailable() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCheckedOut() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

}
