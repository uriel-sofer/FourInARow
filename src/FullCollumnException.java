public class FullCollumnException extends Exception{
    public FullCollumnException(){
        super("Can't insert to the selected row any more tokens");
    }
}