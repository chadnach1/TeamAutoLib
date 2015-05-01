package autolib_catalog;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volume.VolumeInfo.IndustryIdentifiers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class Catalog {

	public static void main(String[] args) throws InterruptedException {
		Scanner in = null;
		Scanner inBT = null;
		Writer out = null;
		boolean shouldQuit = false;
		ArrayList<LibraryBook> library = new ArrayList<LibraryBook>();
		ArrayList<Integer> openShelves = new ArrayList<Integer>();
		for(int i = 1; i < 21; i++) {
			openShelves.add(i);
		}
		try {
			in = new Scanner(System.in);
			//out = new PrintWriter(new FileWriter("/dev/rfcomm0"));
			//inBT = new Scanner(new FileReader("dev/rfcomm0"));
			out = new PrintWriter(new FileWriter("/dev/tty.RNBT-3A6B-RNI-SPP"));
			inBT = new Scanner(new FileReader("/dev/tty.RNBT-3A6B-RNI-SPP"));
			HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			final BooksRequestInitializer KEY_INITIALIZER =
				      new BooksRequestInitializer("AIzaSyD8xdhfkcqi3g9U04kmifPz9RBOLNsNBBk");
			Books builder = new Books.Builder(transport, jsonFactory, null)
				.setApplicationName("Automatic Library")
				.setGoogleClientRequestInitializer(KEY_INITIALIZER)
				.build();
			while(!shouldQuit) {
				System.out.println("What would you like to do?");
				System.out.println("1. List available volumes.");
				System.out.println("2. Add new volumes.");
				System.out.println("3. Check out a book.");
				System.out.println("4. Return a book.");
				System.out.println("5. Exit.");
				System.out.println("6. Eject from a specific slot (testing).");
				System.out.print("\nEnter your selection: ");
				String response = in.nextLine();
				if(response.length() != 1 || Character.isLetter(response.charAt(0))) {
					System.out.println("Invalid response. Please select one of the available options.");
					continue;
				} else {
					int selection = Character.getNumericValue(response.charAt(0));
					switch(selection) {
						case 1:
							if(library.isEmpty() || openShelves.size() == 15) {
								System.out.println("Library is empty.\n");
								continue;
							} else {
								Collections.sort(library);
								int i = 1;
								for(LibraryBook book : library) {
									if(book.isAvailable()) {
										printBookInfo(i, book);
									}
								}
							}
							break;
						case 2:
							System.out.println("Ready for scan.");
							String isbnString = in.nextLine();
							Volume v = backoffRequest(builder.volumes().list("isbn:"+isbnString));
							if(v != null) {
								java.util.List<IndustryIdentifiers> identifiers = v.getVolumeInfo().getIndustryIdentifiers();
								String isbn = null;
								Iterator<IndustryIdentifiers> idIter = identifiers.iterator();
								while(idIter.hasNext()) {
									IndustryIdentifiers id = idIter.next();
									if(id.getType().equals("ISBN_13")) {
										isbn = id.getIdentifier();
										break;
									}
								}
								
								int shelf = getMin(openShelves);
								if(shelf == -1) {
									System.out.println("Can't add volume, shelves are full.\n");
									continue;
								}
								System.out.println("\nAdded new book: " + v.getVolumeInfo().getTitle() + 
										((isbn != null)? " with ISBN " + isbn : ""));
								openShelves.remove(openShelves.indexOf(shelf));
								library.add(new LibraryBook(v,shelf));
								out.write("16\n");
								System.out.println("Please place the book in slot "
										+ Integer.toString(shelf)+" and press the green "
												+ "button when finished.\n");
								out.flush();
								while(true) {
									String stat = inBT.nextLine();
									if(stat.equals("done")) break;
								}; //wait for button
							} else {
								System.out.println("Lookup failed.");
							}
							break;
						case 3:
							if(library.isEmpty() || openShelves.isEmpty()) {
								System.out.println("Library is empty.");
								break;
							}
							System.out.println("Type the name of the book you'd like to check out.\n");
							String query = in.nextLine().toLowerCase();
							Iterator<LibraryBook> bookIter = library.iterator();
							LibraryBook currBook;
							boolean wasFound = false;
							while(bookIter.hasNext()) {
								currBook = bookIter.next();
								if(currBook.getTitle().toLowerCase().equals(query)) {
									wasFound = true;
									if(currBook.isAvailable()) {
										System.out.println("Now retrieving "+ currBook.getTitle()+ ".");
										currBook.setCheckedOut();
										openShelves.add(currBook.getShelfNumber());
										out.write(currBook.getShelfNumber()+"\n");
										out.flush();
										while(true) {
											String stat = inBT.nextLine();
											if(stat.equals("done")) break;
										}; //wait for retrieval to complete
									} else {
										System.out.println("Sorry, that book is checked out.\n");
									}
									break;
								}
							}
							if(!wasFound) {
								System.out.println("Sorry, we don't currently have that book.\n");
							}
							break;
						case 4:
							System.out.println("Please scan the book you're returning.");
							isbnString = in.nextLine();
							v = backoffRequest(builder.volumes().list("isbn:"+isbnString));
							if(v != null) {
								query = v.getVolumeInfo().getTitle().toLowerCase();
								bookIter = library.iterator();
								currBook = null;
								wasFound = false;
								while(bookIter.hasNext()) {
									currBook = bookIter.next();
									if(currBook.getTitle().toLowerCase().equals(query)) {
										wasFound = true;
										if(!currBook.isAvailable()) {
											int shelf = getMin(openShelves);
											if(shelf == -1) {
												System.out.println("Can't return volume, shelves are full.\n");
												break;
											}
											out.write("16\n");
											out.flush();
											System.out.println("Please place the book in slot "
													+ Integer.toString(shelf)+" and press the green "
															+ "button when finished.\n");
											currBook.setAvailable();
											openShelves.remove(new Integer(shelf));
											while(true) {
												String stat = inBT.nextLine();
												if(stat.equals("done")) break;
											}; //wait for button
										} else {
											System.out.println("Sorry, that book is not checked out, so it can't be checked in.\n");
										}
										break;
									}
								}
								if(!wasFound) {
									System.out.println("Sorry, we don't currently have that book in our database.\n");
								}
								break;
							} else {
								System.out.println("Lookup failed.");
							}
							break;
						case 5:
							shouldQuit = true;
							System.out.println("Exiting.");
							break;
						case 6:
							System.out.println("Enter the slot number.");
							String number = in.nextLine();
							out.write(number+"\n");
							out.flush();
							break;
						default:
							break;
					}
				}
				
			}
			
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	/**
	 * Prints information about a LibraryBook.
	 * 
	 * @param index The index of the book in the list of books being printed.
	 * @param book The LibraryBook instance wrapping the Volume for the book to be printed.
	 */
	private static void printBookInfo(int index, LibraryBook book) {
		String authors = book.getAuthors().toString()
				.substring(1, book.getAuthors().toString().length()-1);
		System.out.println("------------------------------------\n"
				+ Integer.toString(index) +". "
				+ book.getTitle() +"\n"
				+ ((book.getAuthors().size() > 1) ? "Authors: " : "Author: ")
				+ authors + "\n"
				+ book.getNumPages() + " pages\n"
				+ "Published in " + book.getPublicationDate() + "\n"
				+ (book.isAvailable() ? "Located on shelf "+book.getShelfNumber()+".\n" : "")
				+ "------------------------------------"
		);
	}

	/** Implements exponential backoff for Google API requests. 
	 * 
	 * @param List list
	 * 	The list Get request to be executed using exponential backoff.
	 * 
	 * @throws InterruptedException */
	public static Volume backoffRequest(List list) throws InterruptedException {
		for(int i = 0; i < 5; i++) {
			try {
				java.util.List<Volume> bookResults = list.execute().getItems();
				
				return bookResults.get(0);
			} catch (GoogleJsonResponseException e) {
				if(e.getStatusCode() == 503) {
					Random r = new Random();
					Thread.sleep((long) (Math.pow(2.0,(double)i)+r.nextInt(1000)));
				}
			} catch (IOException e) {e.printStackTrace();}
		}
		return null;
	}
	
	public static Integer getMin(ArrayList<Integer> arr) {
		Integer currMin = 100; //arbitrary
		for(Integer i : arr) {
			if(i < currMin) currMin = i;
		}
		if(currMin == 100) return new Integer(-1);
		return currMin;
	}
}
