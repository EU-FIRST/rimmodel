package eu.first.RIM;

import java.io.IOException;

import eu.first.RIM.Data;

/**
 * DataSource abstract class. Defines basic methods for obtaining Data objects.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-20
 * 
 */
 public abstract class DataSource {
    
    public abstract boolean hasNext() throws IOException;
    
    public abstract Data getNext() throws IOException; 
    
    public String getLastError() {
        return "";
    }

}
