package eth.epieffe.jwalker.example;

import java.util.Arrays;

/**
 * Rappresenta un'istanza del gioco NPuzzle.
 * Il gioco presenta una matrice n*n dove ogni cella contiene un numero da 1 a n*n-1 e una cella e vuota.
 * Le celle adiacenti a quella vuota possono essere spostate al posto di quella vuota in una mossa.
 * Il gioco è risolto quando tutte le celle sono ordinate e la cella vuota sta nell'ultima posizione.
 */
public class NPuzzle {

    public static final NPuzzleProblem PROBLEM = new NPuzzleProblem();
    private final byte[] table;
    private final byte length;
    private final byte emptyX;
    private final byte emptyY;

    public static NPuzzle newInstance(byte[][] matrix) {
        if (matrix.length < 1 || matrix[0].length != matrix.length) {
            throw new IllegalArgumentException("invalid table size");
        }
        byte length = (byte) matrix.length;
        byte[] flatTable = new byte[length * length];
        byte emptyX = -1;
        byte emptyY = -1;

        for (byte i = 0; i < length; i++) {
            for (byte j = 0; j < length; j++) {
                byte value = matrix[i][j];
                if (value >= length * length) {
                    throw new IllegalArgumentException("invalid table: cell value too high");
                }
                if (value < 1) {
                    if (emptyX >= 0 || emptyY >= 0) {
                        throw new IllegalArgumentException("invalid table: more than one empty cell");
                    }
                    emptyX = j;
                    emptyY = i;
                }
                flatTable[i * length + j] = value;
            }
        }
        if (emptyX < 0 || emptyY < 0) {
            throw new IllegalArgumentException("invalid table: no empty cell");
        }
        return new NPuzzle(flatTable, length, emptyX, emptyY);
    }

    /**
     * @param t: array che rappresenta la configurazione.
     * @param l: dimensione del lato del puzzle (es. 3 per un 3x3).
     * @param x: riga della cella vuota.
     * @param y: colonna della cella vuota.
     *           Si assume che la matrice rappresenti una configurazione valida
     *           e che x e y abbiano valori corretti.
     */
    public NPuzzle(byte[] t, byte l, byte x, byte y) {
        this.table = t;
        this.length = l;
        this.emptyX = x;
        this.emptyY = y;
    }

    public byte getLength() {
        return length;
    }

    public byte getCell(byte row, byte col) {
        return table[row * length + col];
    }

    public byte getEmptyX() {
        return emptyX;
    }

    public byte getEmptyY() {
        return emptyY;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NPuzzle)) {
            return false;
        }
        NPuzzle other = (NPuzzle) o;
        return this.length == other.length && Arrays.equals(this.table, other.table);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(table);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte i = 0; i < length; i++) {
            sb.append("[");
            for (byte j = 0; j < length; j++) {
                sb.append(getCell(i, j));
                if (j < length - 1) sb.append(", ");
            }
            sb.append("]\n");
        }
        return sb.toString().trim();
    }
}
