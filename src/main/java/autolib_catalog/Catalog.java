package autolib_catalog;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.Get;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.BooksScopes;
import com.google.api.services.books.model.Volume;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;
import java.util.Scanner;

public class Catalog {

	public static void main(String[] args) throws InterruptedException {
		Scanner in = null;
		try {
			in = new Scanner(System.in);
			HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			/*GoogleCredential credential = new GoogleCredential.Builder().setTransport(transport)
			        .setJsonFactory(jsonFactory)
			        .setServiceAccountId("661373134575-hervr3tn0iccfe2k3knotrl1ae0gne0d@developer.gserviceaccount.com")
			        .setServiceAccountScopes(BooksScopes.all())
			        .setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
			        .build();*/
			final BooksRequestInitializer KEY_INITIALIZER =
				      new BooksRequestInitializer("AIzaSyD8xdhfkcqi3g9U04kmifPz9RBOLNsNBBk");
			Books builder = new Books.Builder(transport, jsonFactory, null)
				.setApplicationName("Automatic Library")
				.setGoogleClientRequestInitializer(KEY_INITIALIZER)
				.build();
			System.out.println("Ready for scan.");
			String isbnString = in.nextLine();
			backoffRequest(builder.volumes().list("isbn:"+isbnString));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	public static void backoffRequest(List list) throws InterruptedException {
		Exception exception = null;
		for(int i = 0; i < 5; i++) {
			try {
				java.util.List<Volume> bookResults = list.execute().getItems();
				String bookTitle = bookResults.get(0).getVolumeInfo().getTitle();
				System.out.println("Title is " + bookTitle + ".");
				return;
			} catch (GoogleJsonResponseException e) {
				if(e.getStatusCode() == 503) {
					Random r = new Random();
					Thread.sleep((long) (Math.pow(2.0,(double)i)+r.nextInt(1000)));
				}
				exception = e;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(exception != null) {
			exception.printStackTrace();
		}
	}
}
