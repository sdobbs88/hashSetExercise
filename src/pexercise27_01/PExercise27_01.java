/**
 * Class: CSCI2410 Data Structures and Algorithms
 * Instructor: Y. Daniel Liang
 * Description: Adds items to a hash set and prints the items. 
 * Due: 11/7/2016
 *
 * @author Shaun C. Dobbs
 * @version 1.0
 *
 * I pledge by honor that I have completed the programming assignment
 * independently. I have not copied the code from a student or any source. I
 * have not given my code to any student. * Sign here: Shaun C. Dobbs
 */

import java.util.*;
 
public class PExercise27_01 {
 
   public static void main(String[] args) throws CloneNotSupportedException {
        // Create a MyHashSet
        java.util.Collection<String> set = new MyHashSet<>();
        set.add("Smith");
        set.add("Anderson");
        set.add("Lewis");
        set.add("Cook");
        set.add("Smith");
 
        MyHashSet<String> set1 = new MyHashSet<>();
        set1.add("Smith");
        set1.add("Anderson");
        set1.add("Johnson");
        set1.add("Peterson");
        set1.add("Stone");
        System.out.println("set1 is " + set1);
 
        // Test clone
        java.util.Collection<String> set2 = (MyHashSet<String>) (set1.clone());
        set2.add("Jones");
        System.out.println("set1 is " + set1);
        System.out.println("set2 is " + set2);
 
        // Test containsAll
        System.out.println(set1.containsAll(set2));
        System.out.println(set2.containsAll(set2));
 
        // Test addAll
        java.util.Collection<String> set3 = (MyHashSet<String>) (set1.clone());
        set3.addAll(set);
        System.out.println("(union) set3 is " + set3);
 
        // Test removeAll
        java.util.Collection<String> set4 = (MyHashSet<String>) (set1.clone());
        set4.removeAll(set);
        System.out.println("(difference) set4 is " + set4);
 
        // Test retainAll
        java.util.Collection<String> set5 = (MyHashSet<String>) (set1.clone());
        set5.retainAll(set);
        System.out.println("(intersection) set5 is " + set5);
 
        // Test toArray
        Object[] temp = set.toArray();
        System.out.println(Arrays.toString(temp));
 
        // Test toArray(T[] arg)
        String[] temp1 = new String[set.size()];
        temp1 = set.toArray(temp1);
        System.out.println(Arrays.toString(temp1));
    }
 

    static class MyHashSet<E> implements Collection<E>, Cloneable {
 
        @Override
        protected Object clone() throws CloneNotSupportedException {
            MyHashSet<String> cloned = (MyHashSet<String>) super.clone();
            java.util.Collection<String> cloned2 = new MyHashSet<>();
            cloned2.addAll(cloned);
 
            return cloned2;          
 
        }
 
        // Define the default hash table size. Must be a power of 2
        private static int DEFAULT_INITIAL_CAPACITY = 4;
 
        // Define the maximum hash table size. 1 << 30 is same as 2^30
        private static int MAXIMUM_CAPACITY = 1 << 30;
 
        // Current hash table capacity. Capacity is a power of 2
        private int capacity;
 
        // Define default load factor
        private static float DEFAULT_MAX_LOAD_FACTOR = 0.75f;
 
        // Specify a load factor threshold used in the hash table
        private float loadFactorThreshold;
 
        // The number of elements in the set
        private int size = 0;
 
        // Hash table is an array with each cell that is a linked list
        private LinkedList<E>[] table;
 
        /**
         * Construct a set with the default capacity and load factor
         */
        public MyHashSet() {
            this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
        }
 
        /**
         * Construct a set with the specified initial capacity and default load
         * factor
         */
        public MyHashSet(int initialCapacity) {
            this(initialCapacity, DEFAULT_MAX_LOAD_FACTOR);
        }
 
        /**
         * Construct a set with the specified initial capacity and load factor
         */
        public MyHashSet(int initialCapacity, float loadFactorThreshold) {
            if (initialCapacity > MAXIMUM_CAPACITY) {
                this.capacity = MAXIMUM_CAPACITY;
           } else {
                this.capacity = trimToPowerOf2(initialCapacity);
            }
 
            this.loadFactorThreshold = loadFactorThreshold;
            table = new LinkedList[capacity];
        }
 
        @Override
        /**
         * Remove all elements from this set
         */
        public void clear() {
            size = 0;
            removeElements();
        }
 
        @Override
        /**
         * Return true if the element is in the set
         */
        public boolean contains(Object e) {
            int bucketIndex = hash(e.hashCode());
            if (table[bucketIndex] != null) {
                LinkedList<E> bucket = table[bucketIndex];
                return bucket.contains(e);
            }
 
            return false;
        }
 
        @Override
        /**
         * Add an element to the set
         */
        public boolean add(E e) {
            if (contains(e)) // Duplicate element not stored
            {
                return false;
            }
 
            if (size + 1 > capacity * loadFactorThreshold) {
                if (capacity == MAXIMUM_CAPACITY) {
                    throw new RuntimeException("Exceeding maximum capacity");
                }
 
                rehash();
            }
 
            int bucketIndex = hash(e.hashCode());
 
            // Create a linked list for the bucket if it is not created
            if (table[bucketIndex] == null) {
                table[bucketIndex] = new LinkedList<E>();
            }
 
            // Add e to hashTable[index]
            table[bucketIndex].add(e);
 
            size++; // Increase size
 
            return true;
        }
 
        @Override
        /**
         * Remove the element from the set
         */
        public boolean remove(Object e) {
            if (!contains(e)) {
                return false;
            }
 
            int bucketIndex = hash(e.hashCode());
 
            // Create a linked list for the bucket if it is not created
            if (table[bucketIndex] != null) {
                LinkedList<E> bucket = table[bucketIndex];
                bucket.remove(e);
            }
 
            size--; // Decrease size
 
            return true;
        }
 
        @Override
        /**
         * Return true if the set contains no elements
         */
        public boolean isEmpty() {
            return size == 0;
        }
 
        @Override
        /**
         * Return the number of elements in the set
         */
        public int size() {
            return size;
        }
 
        @Override
        /**
         * Return an iterator for the elements in this set
         */
        public java.util.Iterator<E> iterator() {
            return new MyHashSetIterator(this);
        }
 
        /**
         * Inner class for iterator
         */
        private class MyHashSetIterator implements java.util.Iterator<E> {
            // Store the elements in a list
 
            private java.util.ArrayList<E> list;
            private int current = 0; // Point to the current element in list
            private MyHashSet<E> set;
 
            /**
             * Create a list from the set
             */
            public MyHashSetIterator(MyHashSet<E> set) {
                this.set = set;
                list = setToList();
            }
 
            @Override
            /**
             * Next element for traversing?
             */
            public boolean hasNext() {
                return current < list.size();
            }
 
            @Override
            /**
             * Get current element and move cursor to the next
             */
            public E next() {
                return list.get(current++);
            }
 
            @Override
            /**
             * Remove the current element and refresh the list
             */
            public void remove() {
                // Delete the current element from the hash set
                set.remove(list.get(current));
                list.remove(current); // Remove current element from the list
            }
        }
 
        /**
         * Hash function
         */
        private int hash(int hashCode) {
            return supplementalHash(hashCode) & (capacity - 1);
        }
 
        /**
         * Ensure the hashing is evenly distributed
         */
        private static int supplementalHash(int h) {
            h ^= (h >>> 20) ^ (h >>> 12);
            return h ^ (h >>> 7) ^ (h >>> 4);
        }
 
        /**
         * Return a power of 2 for initialCapacity
         */
        private int trimToPowerOf2(int initialCapacity) {
            int capacity = 1;
            while (capacity < initialCapacity) {
                capacity <<= 1;
            }
 
            return capacity;
        }
 
        /**
         * Remove all e from each bucket
         */
        private void removeElements() {
            for (int i = 0; i < capacity; i++) {
                if (table[i] != null) {
                    table[i].clear();
                }
            }
        }
 
        /**
         * Rehash the set
         */
        private void rehash() {
            java.util.ArrayList<E> list = setToList(); // Copy to a list
            capacity <<= 1; // Double capacity     
            table = new LinkedList[capacity]; // Create a new hash table
            size = 0; // Reset size
 
            for (E element : list) {
                add(element); // Add from the old table to the new table
            }
        }
 
        /**
         * Copy elements in the hash set to an array list
         */
        private java.util.ArrayList<E> setToList() {
            java.util.ArrayList<E> list = new java.util.ArrayList<>();
 
            for (int i = 0; i < capacity; i++) {
                if (table[i] != null) {
                    for (E e : table[i]) {
                        list.add(e);
                    }
                }
            }
 
            return list;
        }
 
//        public E get(int index);
        @Override
        public String toString() {
            java.util.ArrayList<E> list = setToList();
            StringBuilder builder = new StringBuilder("[");
 
            // Add the elements except the last one to the string builder
            for (int i = 0; i < list.size() - 1; i++) {
                builder.append(list.get(i) + ", ");
            }
 
            // Add the last element in the list to the string builder
            if (list.size() == 0) {
                builder.append("]");
            } else {
                builder.append(list.get(list.size() - 1) + "]");
            }
 
            return builder.toString();
        }
 
        @Override
        public boolean addAll(Collection<? extends E> arg0) {
            for (Object i : arg0) {
                add((E) i);
            }
            return true;
        }
 
        @Override
        public boolean containsAll(Collection<?> arg0) {
            for (Object i : arg0) {
                if (!contains(i)) {
                    return false;
                }
            }
            return true;
        }
 
        @Override
        public boolean removeAll(Collection<?> arg0) {
            boolean modded = false;
            for (Object i : arg0) {
                if (remove(i)) {
                    modded = true;
                }
            }
            return true;
        }
 
        @Override
        public boolean retainAll(Collection<?> arg0) {
            java.util.ArrayList<E> list = setToList();
            boolean modded = false;
            for (int i = 0; i < list.size(); i++) {
                if (!arg0.contains(list.get(i))) {
                    remove(list.get(i));
                    modded = true;
                }
            }
            return modded;
        }
 
        @Override
        public Object[] toArray() {
            java.util.ArrayList<E> list = setToList();
            Object[] arr = new Object[size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            return arr;
        }
 
        @Override
        public <T> T[] toArray(T[] arg0) {
            java.util.ArrayList<E> list = setToList();
            T[] arr = (T[]) new String[arg0.length];
            for (int i = 0; i < arg0.length; i++) {
                arr[i] = (T) list.get(i);
            }
            return arr;
        }
    }
}