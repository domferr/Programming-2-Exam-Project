package DomenicoFerraro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class HashingDataContainer<E> implements SecureDataContainer<E> {

    private HashMap<String, String> users;
    private HashMap<String, Vector<E>> storage;

    @Override
    public void createUser(String Id, String passw) throws NullPointerException, UserAlreadyExistsException {
        if (Id == null || passw == null)
            throw new NullPointerException();
        if (checkId(Id)) throw new UserAlreadyExistsException();  //Se l'ID esiste già

        users.put(Id, passw);     //Lo aggiungo
    }

    @Override
    public int getSize(String Owner, String passw) throws NullPointerException, UserAccessDeniedException {
        if (Owner == null || passw == null)
            throw new NullPointerException();
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        Vector<E> data = storage.get(Owner);
        if (data != null)
            return data.size();

        return 0;
    }

    @Override
    public boolean put(String Owner, String passw, E data) throws NullPointerException, UserAccessDeniedException {
        if (Owner == null || passw == null || data == null)
            throw new NullPointerException();
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        Vector<E> dataVector = storage.get(Owner);  //Ottengo il vettore dei dati di Owner
        if (dataVector != null)
            return storage.get(Owner).add(data);    // Inserisco il dato

        return false;
    }

    @Override
    public E get(String Owner, String passw, E data) throws NullPointerException, UserAccessDeniedException {
        if (Owner == null || passw == null || data == null)
            throw new NullPointerException();
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        //Ottengo i dati a cui Owner può accedere
        Vector<E> dataVector = storage.get(Owner);

        if (dataVector != null) {
            int index = dataVector.indexOf(data);
            if (index >= 0)
                return dataVector.get(index);
        }

        //Altrimenti restituisco null
        return null;
    }

    @Override
    public E remove(String Owner, String passw, E data) throws NullPointerException, UserAccessDeniedException {
        if (Owner == null || passw == null || data == null)
            throw new NullPointerException();
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        //Ottengo i dati a cui Owner può accedere
        Vector<E> dataVector = storage.get(Owner);

        if (dataVector != null) {
            int index = dataVector.indexOf(data);
            if (index >= 0) {
                return dataVector.remove(index);
            }
        }
        //Se non l'ho trovato allora restituisco null
        return null;
    }

    @Override
    public void copy(String Owner, String passw, E data) throws NullPointerException, UserAccessDeniedException, IllegalArgumentException {
        if (Owner == null || passw == null || data == null)
            throw new NullPointerException();
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        //Ottengo i dati a cui Owner può accedere
        Vector<E> dataVector = storage.get(Owner);

        if (dataVector != null){
            int index = dataVector.indexOf(data);
            if (index >= 0)
                dataVector.add(dataVector.get(index));
            else
                throw new IllegalArgumentException();

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void share(String Owner, String passw, String Other, E data) throws NullPointerException, IllegalArgumentException, UserAccessDeniedException, UserNotExistsException {
        if (Owner == null || passw == null || Other == null || data == null)
            throw new NullPointerException();
        if (!checkId(Other)) throw new UserNotExistsException();
        if (Owner.equals(Other)) throw new IllegalArgumentException();  //l'utente non deve condividere il dato con se stesso
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        //Ottengo i dati a cui Owner può accedere
        Vector<E> ownerDataVector = storage.get(Owner);
        Vector<E> otherDataVector = storage.get(Other);

        if (ownerDataVector != null && otherDataVector != null) {
            int index = ownerDataVector.indexOf(data);
            if (index >= 0){
                otherDataVector.add(ownerDataVector.get(index));
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Iterator<E> getIterator(String Owner, String passw) throws NullPointerException, UserAccessDeniedException {
        if (Owner == null || passw == null)
            throw new NullPointerException();
        if (!logIn(Owner, passw)) throw new UserAccessDeniedException();  //Controllo di identità fallito

        //Inizializzo una collezione
        Vector<E> userData = storage.get(Owner);

        //Ritorno un generatore (che non supporta remove()) dei dati presenti nella collezione appena creata
        return new UserDataGen<E>(userData);
    }

    /** EFFECTS: Restituisce true se l'id è già utilizzato, false altrimenti. */
    private boolean checkId(String id){
        return users.containsKey(id);
    }

    /** EFFECTS: Restituisce true se vengono rispettati i controlli di identità, ovvero se esiste un utente con stesso id e passw, false altrimenti. */
    private boolean logIn(String id, String passw){
        if (users.get(id) != null)
            return users.get(id).equals(passw);
        return false;
    }

    private static class UserDataGen<T> implements Iterator<T> {
        //OVERVIEW: Iterator dei dati a cui uno specifico utente può accedere

        //TYPICAL ELEMENT: <{userData.get(i) | 0 <= i < size}, size, currentIndex>

        //IR: userData != null && size != null && size >= 0 && 0 <= currentIndex < size && size == userData.size()
        //    && userData.get(i) != null for all 0 <= i < size

        private Vector<T> userData;
        private int size;
        private int currentIndex;

        //EFFECTS: Costruisce this
        public UserDataGen(Vector<T> userData) throws NullPointerException {
            if (userData == null) throw new NullPointerException();
            this.userData = new Vector<>(userData);
            currentIndex = 0;
            size = userData.size();
        }

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public T next() {
            return userData.get(currentIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
