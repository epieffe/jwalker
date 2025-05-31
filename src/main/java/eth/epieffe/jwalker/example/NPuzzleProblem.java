package eth.epieffe.jwalker.example;

import eth.epieffe.jwalker.Move;
import eth.epieffe.jwalker.Problem;

import java.util.ArrayList;
import java.util.List;

public class NPuzzleProblem implements Problem<NPuzzle> {

    /**
     * Restituisce una lista contenente tutte le configurazioni ottenibili
     * facendo una singola mossa a partire dalla configurazione attuale.
     */
    @Override
    public List<Move<NPuzzle>> getMoves(NPuzzle config) {
        byte emptyY = config.getEmptyY();
        byte emptyX = config.getEmptyX();
        byte length = config.getLength();
        List<Move<NPuzzle>> moveList = new ArrayList<>(4);
        if (emptyY > 0) {
            // move up
            byte[] newTable = copyTable(config);
            int from = (emptyY - 1) * length + emptyX;
            int to = emptyY * length + emptyX;
            swap(newTable, from, to);
            moveList.add(new Move<>("UP", 1, new NPuzzle(newTable, length, emptyX, (byte) (emptyY - 1))));
        }
        if (emptyY < length - 1) {
            // move down
            byte[] newTable = copyTable(config);
            int from = (emptyY + 1) * length + emptyX;
            int to = emptyY * length + emptyX;
            swap(newTable, from, to);
            moveList.add(new Move<>("DOWN", 1, new NPuzzle(newTable, length, emptyX, (byte) (emptyY + 1))));
        }
        if (emptyX > 0) {
            // move left
            byte[] newTable = copyTable(config);
            int from = emptyY * length + (emptyX - 1);
            int to = emptyY * length + emptyX;
            swap(newTable, from, to);
            moveList.add(new Move<>("LEFT", 1, new NPuzzle(newTable, length, (byte) (emptyX - 1), emptyY)));
        }
        if (emptyX < length - 1) {
            // move right
            byte[] newTable = copyTable(config);
            int from = emptyY * length + (emptyX + 1);
            int to = emptyY * length + emptyX;
            swap(newTable, from, to);
            moveList.add(new Move<>("RIGHT", 1, new NPuzzle(newTable, length, (byte) (emptyX + 1), emptyY)));
        }

        return moveList;
    }

    private void swap(byte[] array, int i, int j) {
        byte temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }


    @Override
    public boolean isSolved(NPuzzle config) {
        int length = config.getLength();
        int v = 1;
        for (byte i = 0; i < length; i++) {
            for (byte j = 0; j < length; j++) {
                if (i == length - 1 && j == length - 1) {
                    if (config.getCell(i, j) > 0) {
                        return false;
                    }
                } else if (config.getCell(i, j) != v) {
                    return false;
                }
                v++;
            }
        }
        return true;
    }

    private byte[] copyTable(NPuzzle config) {
        int length = config.getLength();
        byte[] newTable = new byte[length * length];
        for (byte i = 0; i < length; i++) {
            for (byte j = 0; j < length; j++) {
                newTable[i * length + j] = config.getCell(i, j);
            }
        }
        return newTable;
    }
}
