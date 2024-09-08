package edu.java.fintechcourse2024.hw3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);
        System.out.print("Список после добавления элементов: ");
        list.printList();

        System.out.println("Элемент на индексе 1: " + list.get(1));


        list.remove(1);
        System.out.print("Список после удаления элемента с индекса 1: ");
        list.printList();


        System.out.println("Список содержит 2: " + list.contains(2));
        System.out.println("Список содержит 3: " + list.contains(3));


        List<Integer> newElements = Arrays.asList(4, 5, 6);
        list.addAll(newElements);
        System.out.print("Список после добавления всех элементов из другого списка: ");
        list.printList();


        list.insert(7, 0);
        System.out.print("Список после вставки 7 в начало: ");
        list.printList();

        list.insert(8, 3);
        System.out.print("Список после вставки 8 на индекс 3: ");
        list.printList();

        list.insert(9, list.getSize());
        System.out.print("Список после вставки 9 в конец: ");
        list.printList();


        Stream<Integer> elementStream = Stream.of(1, 2, 3);

        CustomLinkedList<Integer> customList = elementStream.reduce(
                new CustomLinkedList<>(),
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

        System.out.print("Проверка корректной работы customList");
        customList.add(4);
        customList.add(5);
        System.out.print("Список после добавления элементов: ");
        customList.printList();

        System.out.println("Элемент на индексе 1: " + customList.get(1));


        customList.remove(1);
        System.out.print("Список после удаления элемента с индекса 1: ");
        customList.printList();


        System.out.println("Список содержит 2: " + customList.contains(2));
        System.out.println("Список содержит 3: " + customList.contains(3));


        List<Integer> newElementsCustom = Arrays.asList(6, 7, 8);
        customList.addAll(newElementsCustom);
        System.out.print("Список после добавления всех элементов из другого списка: ");
        customList.printList();


        customList.insert(9, 0);
        System.out.print("Список после вставки 9 в начало: ");
        customList.printList();

        customList.insert(10, 3);
        System.out.print("Список после вставки 10 на индекс 3: ");
        customList.printList();

        customList.insert(11, customList.getSize());
        System.out.print("Список после вставки 11 в конец: ");
        customList.printList();


        System.out.print("Список после преобразования стрима: ");
        customList.printList();
    }
}
