/*
 * (C) Copyright 2014-2016, by Dimitrios Michail
 *
 * JHeaps Library
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file has been modified by Epifanio Ferrari in 2025 to adapt it for use in JWalker.
 */
package eth.epieffe.jwalker.algorithm;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/**
 * Fibonacci heaps. The heap is sorted according to its key values.
 *
 * <p>
 * This implementation provides amortized O(1) time for operations that do not
 * involve deleting an element such as {@code insert}, and {@code decreaseKey}.
 * Operation {@code findMin} is worst-case O(1). Operations {@code deleteMin}
 * and {@code delete} are amortized O(log(n)).
 *
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access a heap concurrently, and at least one of the threads
 * modifies the heap structurally, it <em>must</em> be synchronized externally.
 * (A structural modification is any operation that adds or deletes one or more
 * elements or changing the key of some element.) This is typically accomplished
 * by synchronizing on some object that naturally encapsulates the heap.
 *
 * @param <V> the type of values maintained by this heap
 *
 * @author Dimitrios Michail
 */
class FibonacciHeap<V> implements Serializable {

    /**
     * Size of consolidation auxiliary array. Computed for number of elements
     * equal to {@link Long#MAX_VALUE}.
     */
    private static final int AUX_CONSOLIDATE_ARRAY_SIZE = 91;

    /**
     * Auxiliary array for consolidation
     */
    private final Handle<V>[] aux;

    /**
     * The root with the minimum key
     */
    private Handle<V> minRoot;

    /**
     * Number of roots in the root list
     */
    private int roots;

    /**
     * Size of the heap
     */
    private long size;

    /**
     * Constructs a new, empty heap.
     */
    @SuppressWarnings("unchecked")
    public FibonacciHeap() {
        this.minRoot = null;
        this.roots = 0;
        this.size = 0;
        this.aux = (Handle<V>[]) Array.newInstance(Handle.class, AUX_CONSOLIDATE_ARRAY_SIZE);
    }

    /**
     * Insert a new element into the heap.
     *
     * @param key the element's key
     * @param value the element's value
     *
     * @return a handle for the newly added element
     */
    public Handle<V> insert(double key, V value) {
        Handle<V> n = new Handle<>(this, key, value);
        addToRootList(n);
        size++;
        return n;
    }

    /**
     * Find an element with the minimum key.
     *
     * @return a handle to an element with minimum key
     */
    public Handle<V> findMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return minRoot;
    }

    /**
     * Delete and return an element with the minimum key. If multiple such
     * elements exists, only one of them will be deleted. After the element is
     * deleted the handle is invalidated and only method {@link Handle#getKey()}
     * and {@link Handle#getValue()} can be used.
     *
     * @return a handle to the deleted element with minimum key
     */
    public Handle<V> deleteMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        Handle<V> z = minRoot;

        // move z children into root list
        Handle<V> x = z.child;
        while (x != null) {
            Handle<V> nextX = (x.next == x) ? null : x.next;

            // clear parent
            x.parent = null;

            // remove from child list
            x.prev.next = x.next;
            x.next.prev = x.prev;

            // add to root list
            x.next = minRoot.next;
            x.prev = minRoot;
            minRoot.next = x;
            x.next.prev = x;
            roots++;

            // advance
            x = nextX;
        }
        z.degree = 0;
        z.child = null;

        // remove z from root list
        z.prev.next = z.next;
        z.next.prev = z.prev;
        roots--;

        // decrease size
        size--;

        // update minimum root
        if (z == z.next) {
            minRoot = null;
        } else {
            minRoot = z.next;
            consolidate();
        }

        // clear other fields
        z.next = null;
        z.prev = null;

        return z;
    }

    /**
     * Returns {@code true} if this heap is empty.
     *
     * @return {@code true} if this heap is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the heap.
     *
     * @return the number of elements in the heap
     */
    public long size() {
        return size;
    }

    /**
     * Clear all the elements of the heap. After calling this method all handles
     * should be considered invalidated and the behavior of methods
     * {@link Handle#decreaseKey(double)} and {@link Handle#delete()} is
     * undefined.
     */
    public void clear() {
        minRoot = null;
        roots = 0;
        size = 0;
    }

    // --------------------------------------------------------------------
    /**
     * A heap element handle. Allows someone to address an element already in a
     * heap and perform additional operations.
     *
     * @param <V> the type of values maintained by this heap
     */
    public static class Handle<V> implements Serializable {
        private final FibonacciHeap<V> heap;

        private double key;
        private V value;
        private Handle<V> parent; // parent
        private Handle<V> child; // any child
        private Handle<V> next; // younger sibling
        private Handle<V> prev; // older sibling
        private int degree; // number of children
        private boolean mark; // marked or not

        Handle(FibonacciHeap<V> heap, double key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.parent = null;
            this.child = null;
            this.next = null;
            this.prev = null;
            this.degree = 0;
            this.mark = false;
        }

        /**
         * Return the key of the element.
         *
         * @return the key of the element
         */
        public double getKey() {
            return key;
        }

        /**
         * Return the value of the element.
         *
         * @return the value of the element
         */
        public V getValue() {
            return value;
        }

        /**
         * Set the value of the element.
         *
         * @param value the new value
         */
        public void setValue(V value) {
            this.value = value;
        }

        /**
         * Decrease the key of the element.
         *
         * @param newKey the new key
         *
         * @throws IllegalArgumentException
         *             if the new key is larger than the old key or the element has
         *             already been deleted.
         */
        public void decreaseKey(double newKey) {
            heap.decreaseKey(this, newKey);
        }

        /**
         * Delete the element from the heap that it belongs.
         *
         * @throws IllegalArgumentException
         *             in case the element has already been deleted.
         */
        public void delete() {
            if (this.next == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            heap.forceDecreaseKeyToMinimum(this);
            heap.deleteMin();
        }
    }

    /*
     * Decrease the key of a node.
     */
    private void decreaseKey(Handle<V> n, double newKey) {
        if (newKey > n.key) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        if (n.next == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        if (newKey == n.key) {
            return;
        }
        n.key = newKey;

        // if not root and heap order violation
        Handle<V> y = n.parent;
        if (y != null && n.key < y.key) {
            cut(n, y);
            cascadingCut(y);
        }

        // update minimum root
        if (n.key < minRoot.key) {
            minRoot = n;
        }
    }

    /*
     * Decrease the key of a node to the minimum. Helper function for performing
     * a delete operation. Does not change the node's actual key, but behaves as
     * the key is the minimum key in the heap.
     */
    private void forceDecreaseKeyToMinimum(Handle<V> n) {
        // if not root
        Handle<V> y = n.parent;
        if (y != null) {
            cut(n, y);
            cascadingCut(y);
        }
        minRoot = n;
    }

    /*
     * Consolidate: Make sure each root tree has a distinct degree.
     */
    private void consolidate() {
        int maxDegree = -1;

        // for each node in root list
        int numRoots = roots;
        Handle<V> x = minRoot;
        while (numRoots > 0) {
            Handle<V> nextX = x.next;
            int d = x.degree;

            while (true) {
                Handle<V> y = aux[d];
                if (y == null) {
                    break;
                }

                // make sure x's key is smaller
                if (y.key < x.key) {
                    Handle<V> tmp = x;
                    x = y;
                    y = tmp;
                }

                // make y a child of x
                link(y, x);

                aux[d] = null;
                d++;
            }

            // store result
            aux[d] = x;

            // keep track of max degree
            if (d > maxDegree) {
                maxDegree = d;
            }

            // advance
            x = nextX;
            numRoots--;
        }

        // recreate root list and find minimum root
        minRoot = null;
        roots = 0;
        for (int i = 0; i <= maxDegree; i++) {
            if (aux[i] != null) {
                addToRootList(aux[i]);
                aux[i] = null;
            }
        }
    }

    /*
     * Remove node y from the root list and make it a child of x. Degree of x
     * increases by 1 and y is unmarked if marked.
     */
    private void link(Handle<V> y, Handle<V> x) {
        // remove from root list
        y.prev.next = y.next;
        y.next.prev = y.prev;

        // one less root
        roots--;

        // clear if marked
        y.mark = false;

        // hang as x's child
        x.degree++;
        y.parent = x;

        Handle<V> child = x.child;
        if (child == null) {
            x.child = y;
            y.next = y;
            y.prev = y;
        } else {
            y.prev = child;
            y.next = child.next;
            child.next = y;
            y.next.prev = y;
        }
    }

    /*
     * Cut the link between x and its parent y making x a root.
     */
    private void cut(Handle<V> x, Handle<V> y) {
        // remove x from child list of y
        x.prev.next = x.next;
        x.next.prev = x.prev;
        y.degree--;
        if (y.degree == 0) {
            y.child = null;
        } else if (y.child == x) {
            y.child = x.next;
        }

        // add x to the root list
        x.parent = null;
        addToRootList(x);

        // clear if marked
        x.mark = false;
    }

    /*
     * Cascading cut until a root or an unmarked node is found.
     */
    private void cascadingCut(Handle<V> y) {
        Handle<V> z;
        while ((z = y.parent) != null) {
            if (!y.mark) {
                y.mark = true;
                break;
            }
            cut(y, z);
            y = z;
        }
    }

    /*
     * Add a node to the root list and update the minimum.
     */
    private void addToRootList(Handle<V> n) {
        if (minRoot == null) {
            n.next = n;
            n.prev = n;
            minRoot = n;
            roots = 1;
        } else {
            n.next = minRoot.next;
            n.prev = minRoot;
            minRoot.next.prev = n;
            minRoot.next = n;

            if (n.key < minRoot.key) {
                minRoot = n;
            }
            roots++;
        }
    }
}
