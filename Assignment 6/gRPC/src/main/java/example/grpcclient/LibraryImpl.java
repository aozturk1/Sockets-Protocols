package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import service.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryImpl extends LibraryGrpc.LibraryImplBase implements Serializable {

    List<Book> books;
    public static String file = "books.data";

    public LibraryImpl() {
        this.books = new ArrayList<Book>();
    }

    @Override
    public void borrow (BorrowRequest request, StreamObserver<BorrowResponse> responseObserver) {
        String has = request.getHas();
        String title = request.getTitle();

        boolean available = false;
        boolean exist = false;
        Book bookCopy = null;

        //Determine if the book exists
        for (Book book : books) {
            if (book.getTitle().equals(title)){
                //Determine if the book is already borrowed by someone else
                exist = true;
                bookCopy = book;
                if (bookCopy.getHas().equals("nobody")){
                    available = true;
                    break;
                }
                break;
            }
        }

        BorrowResponse.Builder builder = BorrowResponse.newBuilder()
                .setIsSuccess(false);
        //book exists
        if (exist){
            //book is available
            if (available) {
                builder.setIsSuccess(true);
                builder.setMessage("Happy Reading!");
                Book newBook = Book.newBuilder()
                        .setTitle(bookCopy.getTitle())
                        .setAuthor(bookCopy.getAuthor())
                        .setHas(has)
                        .setGenre(bookCopy.getGenre()).build();
                books.add(newBook);
                books.remove(bookCopy);
            //book is not available
            } else {
                builder.setError("Oops, You can't borrow this book at the moment!");
            }
        //book doesn't exist
        } else {
            builder.setError("Oops, No such book!");
        }

        //Send the response back to the client
        BorrowResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        save();
    }

    public static LibraryImpl load() {
        LibraryImpl loadLibraryImpl;
        try(FileInputStream in = new FileInputStream(file); ObjectInputStream os = new ObjectInputStream(in)) {
            loadLibraryImpl = (LibraryImpl) os.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LibraryImpl();
        } return loadLibraryImpl;
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(file); ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void donate (DonateRequest request, StreamObserver<DonateResponse> responseObserver) {
        String title = request.getTitle();
        String author = request.getAuthor();
        String has = request.getHas();
        has = "nobody";
        String genre = request.getGenre();

        DonateResponse.Builder builder = DonateResponse.newBuilder()
                .setIsSuccess(false);
        //book has title
        if (title != null) {
            Book book = Book.newBuilder()
                    .setTitle(title)
                    .setAuthor(author)
                    .setHas(has)
                    .setGenre(genre).build();
            books.add(book);

            builder.setIsSuccess(true);
            builder.setMessage("Thank you for donating "+ title +" by " + author + "!");
        //book has no title
        } else {
            builder.setError("Oops, Issue looking up the book, please try again!");
        }

        //Send the response back to the client
        DonateResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        save();
    }

    @Override
    public void books (Empty request, StreamObserver<BooksResponse> responseObserver) {
        //Get all books
        //Construct a BooksResponse with the matching books
        BooksResponse.Builder builder = BooksResponse.newBuilder()
                .setIsSuccess(true);
        if (books.isEmpty()) {
            builder.setIsSuccess(false);
            builder.setError("No books available right now!");
        } else {
            builder.addAllBooks(books);
        }
        BooksResponse response = builder.build();

        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
