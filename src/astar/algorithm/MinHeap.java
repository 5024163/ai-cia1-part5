package astar.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A binary min-heap (priority queue).
 * pop() always returns the smallest item in O(log n) time, instead of the
 * O(n) scan that the standard version performs on a plain list.
 */
final class MinHeap<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<T> comparator;

    MinHeap(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    int size() {
        return items.size();
    }

    void push(T item) {
        items.add(item);
        siftUp(items.size() - 1);
    }

    /** Removes and returns the smallest item, or null if the heap is empty. */
    T pop() {
        if (items.isEmpty()) {
            return null;
        }
        T top = items.get(0);
        T last = items.remove(items.size() - 1);
        if (!items.isEmpty()) {
            items.set(0, last);
            siftDown(0);
        }
        return top;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(items.get(index), items.get(parent)) >= 0) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int n = items.size();
        while (true) {
            int left = 2 * index + 1;
            int right = left + 1;
            int smallest = index;
            if (left < n && comparator.compare(items.get(left), items.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < n && comparator.compare(items.get(right), items.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int a, int b) {
        T temp = items.get(a);
        items.set(a, items.get(b));
        items.set(b, temp);
    }
}
