package Users;

import DB.Books_DB;
import SettersAndGetters.Books;

public class SearchByName {
    Books bookName;
    Integer Number;
    Integer pos;
    public SearchByName(Books book, Integer Number, Integer pos){
        this.bookName = book;
        this.Number = Number;
        this.pos = pos;
    }
//    public static int SearchBooks(Books bookName){
//
//    }
//    public boolean IsAvailable(){
//        return Books_DB.
//    }

    public Books getBookName() {
        return bookName;
    }

    public Integer getNumber() {
        return Number;
    }

    public Integer getPos() {
        return pos;
    }

    public SearchByName(){}

}
