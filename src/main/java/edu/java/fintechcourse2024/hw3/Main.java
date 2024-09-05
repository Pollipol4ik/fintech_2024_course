package edu.java.fintechcourse2024.hw3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();

        // Тестирование метода add
        list.add(1);
        list.add(2);
        list.add(3);
        System.out.print("Список после добавления элементов: ");
        list.printList(); // Ожидается: 1 2 3

        // Тестирование метода get
        System.out.println("Элемент на индексе 1: " + list.get(1)); // Ожидается: 2

        // Тестирование метода remove
        list.remove(1);
        System.out.print("Список после удаления элемента с индекса 1: ");
        list.printList(); // Ожидается: 1 3

        // Тестирование метода contains
        System.out.println("Список содержит 2: " + list.contains(2)); // Ожидается: false
        System.out.println("Список содержит 3: " + list.contains(3)); // Ожидается: true

        // Тестирование метода addAll
        List<Integer> newElements = Arrays.asList(4, 5, 6);
        list.addAll(newElements);
        System.out.print("Список после добавления всех элементов из другого списка: ");
        list.printList(); // Ожидается: 1 3 4 5 6

        // Тестирование метода insert
        list.insert(7, 0); // Вставка в начало списка
        System.out.print("Список после вставки 7 в начало: ");
        list.printList(); // Ожидается: 7 1 3 4 5 6

        list.insert(8, 3); // Вставка в середину списка (на индекс 3)
        System.out.print("Список после вставки 8 на индекс 3: ");
        list.printList(); // Ожидается: 7 1 3 8 4 5 6

        list.insert(9, list.getSize()); // Вставка в конец списка
        System.out.print("Список после вставки 9 в конец: ");
        list.printList(); // Ожидается: 7 1 3 8 4 5 6 9

        // Преобразование стрима в CustomLinkedList с использованием reduce
        Stream<Integer> elementStream = Stream.of(10, 11, 12);

        CustomLinkedList<Integer> customList = elementStream.reduce(
                new CustomLinkedList<>(), // Инициализация пустого CustomLinkedList
                (list1, element) -> {
                    list1.add(element);
                    return list1;
                },
                (list1, list2) -> {
                    List<Integer> tempList = new ArrayList<>();
                    for (int i = 0; i < list2.getSize(); i++) {
                        tempList.add(list2.get(i));
                    }
                    list1.addAll(tempList);
                    return list1;
                }
        );

        // Вывод результата
        System.out.print("Список после преобразования стрима: ");
        customList.printList(); // Ожидается вывод: 10 11 12
    }
}
