package DomenicoFerraro;

import java.util.Vector;

public class SharedData<E> {

    private E data;
    private String ownerId;
    private Vector<String> othersIDs;

    public SharedData(E data, String ownerId) {
        if (data == null) throw new NullPointerException();
        if (ownerId == null) throw new NullPointerException();
        this.data = data;
        this.ownerId = ownerId;
        othersIDs = new Vector<>();
    }

    public E getData(){
        return data;
    }

    public void setData(E newValue){
        data = newValue;
    }

    /** Restituisce true se l'id dell'utente passato per argomento può leggere il dato. */
    public boolean canGetData(String id){
        return id.equals(ownerId);
        //return othersIDs.contains(ownerId);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Vector<String> getOthersID() {
        return othersIDs;
    }

    public String toString(){
        return data.toString();
    }

    //TODO aggiungere il metodo per condividere this con un utente, quindi inserendo l'Id dell'utente in othersIDs
}
