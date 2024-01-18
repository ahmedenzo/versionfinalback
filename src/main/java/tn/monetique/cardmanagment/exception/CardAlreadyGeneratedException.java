package tn.monetique.cardmanagment.exception;

public class CardAlreadyGeneratedException extends RuntimeException {
    public CardAlreadyGeneratedException(String message) {
        super(message);
    }
}
