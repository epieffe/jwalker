package eth.epieffe.jwalker.nqueens;

import eth.epieffe.jwalker.Graph;

import java.util.Arrays;
import java.util.Random;

/**
 * Rappresenta un'istanza del gioco NQueens.
 * il gioco consiste nel posizionare n regine su una scacchiera n*n in modo che
 * non si attacchino a vicenda.
 * nb: Siccome due regine non potranno mai stare sulla stessa colonna
 * possiamo fissare ogni regina su una colonna per ridurre il numero delle possibili configurazioni!
 */
public class NQueens {

    public static final Graph<NQueens> GRAPH = new NQueensGraph();

    private static final Random random = new Random();

    /**
     * Segna la posizione della regina su ogni colonna.
     */
    private final int[] posArray;

    public NQueens(int[] pa) {
        posArray = pa;
    }

    public static NQueens newInstance(int... pa) {
        for (int v : pa) {
            if (v < 0 || v >= pa.length) {
                throw new IllegalArgumentException();
            }
        }
        return new NQueens(pa);
    }

    /**
     * Ritorna una nuova istanza casuale su una scacchiera di larghezza l.
     */
    public static NQueens newRandomInstance(int l) {
        int[] pa = new int[l];
        for (int i = 0; i < l; i++) {
            int randInt = random.nextInt(l);
            pa[i] = randInt;
        }
        return new NQueens(pa);
    }

    /**
     * Ritorna la posizione della regina nella colonna col.
     */
    public int row(int col) {
        return posArray[col];
    }

    /**
     * Ritorna la larghezza della scacchiera.
     */
    public int size() {
        return posArray.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(posArray);
    }
}
