/* Copyright 2013 Yamana Laboratory, Waseda University
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
 */

package jp.queuelinker.system.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @param <T>
 */
public class ObjectLinkedList<T> implements Iterable<T> {
    /**
     * 
     */
    private final ObjectLinkedListElement<T> head;

    /**
     * 
     */
    private int size;

    /**
     * 
     */
    public ObjectLinkedList() {
        head = new ObjectLinkedListElement<T>(null);
        head.next = head;
        head.prev = head;
    }

    public ObjectLinkedListElement<T> addFirst(T e) {
        ObjectLinkedListElement<T> newElement = new ObjectLinkedListElement<T>(e);
        return addFirst(newElement);
    }

    public ObjectLinkedListElement<T> addLast(T e) {
        ObjectLinkedListElement<T> newElement = new ObjectLinkedListElement<T>(e);
        return addLast(newElement);
    }

    public ObjectLinkedListElement<T> addFirst(ObjectLinkedListElement<T> newElement) {
        head.next.prev = newElement;
        newElement.next = head.next;
        newElement.prev = head;
        head.next = newElement;

        size++;

        return newElement;
    }

    public ObjectLinkedListElement<T> addLast(ObjectLinkedListElement<T> newElement) {
        head.prev.next = newElement;
        newElement.prev = head.prev;
        newElement.next = head;
        head.prev = newElement;

        size++;

        return newElement;
    }

    public ObjectLinkedListElement<T> remove(ObjectLinkedListElement<T> element) {
        element.prev.next = element.next;
        element.next.prev = element.prev;
        element.next = null;
        element.prev = null;

        size--;

        return element;
    }

    public void clear() {
        head.next = head;
        head.prev = head;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return head.next == head;
    }

    public T poolFirst() {
        if (isEmpty())
            return null;

        final ObjectLinkedListElement<T> first = head.next;
        remove(first);
        return first.object;
    }

    public T peekFirst() {
        if (isEmpty())
            return null;

        return head.next.object;
    }

    @Override
    public Iterator<T> iterator() {
        return new ObjectLinkedListIterator();
    }

    public class ObjectLinkedListIterator implements Iterator<T> {
        private ObjectLinkedListElement<T> currentElement;

        private T lastReturn;

        ObjectLinkedListIterator() {
            currentElement = ObjectLinkedList.this.head;
        }

        @Override
        public boolean hasNext() {
            return currentElement.next != head;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException(); // TODO: May I use this
                                                    // exception?

            currentElement = currentElement.next;
            lastReturn = currentElement.object;
            return currentElement.object;
        }

        @Override
        public void remove() {
            if (lastReturn == null)
                throw new NoSuchElementException(); // TODO: May I use this
                                                    // exception?

            final ObjectLinkedListElement<T> temp = currentElement.prev;
            ObjectLinkedList.this.remove(currentElement);
            currentElement = temp;
            lastReturn = null;
        }
    }
}
