package in.edu.kristujayanti.exception;

import com.mongodb.MongoException;
public class DataAccessException extends MongoException{
    public DataAccessException(String msg) {
        super(msg);
}
}
