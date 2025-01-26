public class FullRowException extends Exception{
    public FullRowException(){
        super("Can't insert to the selected row any more tokens");
    }
}
