package com.test;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

public class Task1 {
	public static ExecutorService Ws = Executors.newCachedThreadPool(); // Параллельное выполнение
	public static List<Callable<Integer[]>> tasks = new ArrayList<Callable<Integer[]>>(); // Список процессов
	 
	/**
	 * _merge последовательное слияние
	 * @param A левая часть
	 * @param B правая часть
	 * @return сортированный объединенный массив
	 */
	public static Integer[] _merge(Integer[] A, Integer[] B, int n, int m) {
		// Массив содержит n элементов из A и m из B
		Integer[] C = new Integer[n + m];

		// Слияние
		// В цикле находим позицию i-го элемента A в объединенном массиве
		for (int i = 0; i < n; i++) {
			int x = A[i];
			var a = W.lowerBound(A, n, x);
			var b = W.lowerBound(B, m, x);
			C[a + b] = x;
		}
		// В цикле находим позицию i-го элемента B в объединенном массиве
		for (int i = 0; i < m; i++) {
			int x = B[i];
			var a = W.lowerBound(A, n, x);
			var b = W.lowerBound(B, m, x);
			C[a + b] = x;
		}
 		return C;
	}

	/**
	 * _mergeSort последовательная сортировка
	 * @param A исходный массив
	 * @return сортированный массив
	 */
	public static Integer[] _mergeSort(Integer[] A, Integer n) {
		if (n == 1)
			return A;

		// Инициализация частей массива
		Integer l[] = new Integer[n / 2];
		Integer r[] = new Integer[n - n / 2];
		for (Integer i = 0; i < n / 2; i++) {
			l[i] = A[i];
		}
		for (int i = 0; i < n - n / 2; i++) {
			r[i] = A[i + n / 2];
		}

		// Сортировка каждой части
		Integer[] L = _mergeSort(l, n / 2);
		Integer[] R = _mergeSort(r, n - n / 2);

		// Слияние частей
		return _merge(L, R, n / 2, n - n / 2);
	}

	/**
	 * merge параллельное слияние
	 * @return сортированный массив
	 */
	public static Integer[] merge(Integer[] A, Integer[] B, int n, int m) throws InterruptedException, ExecutionException {
		// Массив содержит n элементов из A и m из B
		Integer[] C = new Integer[n + m];

		// Инициализация процессов
		Callable<Integer[]> a = new Callable<Integer[]>() {
			@Override
			public Integer[] call() throws Exception {
				// Вычисление левой части
				return new Wa(A, B, C).compute(A, B, C);
			}
		};
		Callable<Integer[]> b = new Callable<Integer[]>() {
			@Override
			public Integer[] call() throws Exception {
				// Вычисление правой части
				return new Wb(A, B, C).compute(A, B, C);
			}
		};

		// Добавление процессов в список
		tasks.add(a);
		tasks.add(b);
 		return C;
	}

	/**
	 * mergeSort параллельная сортировка
	 * @param A исходный массив
	 * @return сортированный массив
	 */
	public static Integer[] mergeSort(Integer[] A, Integer n) {
		if (n == 1) {
			return A;
		}

		// Инициализация частей массива
		Integer l[] = new Integer[n / 2];
		Integer r[] = new Integer[n - n / 2];
		for (Integer i = 0; i < n / 2; i++) {
			l[i] = A[i];
		}
		for (int i = 0; i < n - n / 2; i++) {
			r[i] = A[i + n / 2];
		}

		// Сортировка каждой части
		Integer[] L = mergeSort(l, n / 2);
		Integer[] R = mergeSort(r, n - n / 2);

		// Слияние
		try {
			return merge(L, R, n / 2, n - n / 2);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * elapsedTime затраченное время
	 * @param f метод
	 * @param args аргументы
	 * @return время
	 */
	static long elapsedTime(Method f, Object... args) throws InvocationTargetException, IllegalAccessException {
		long start = System.currentTimeMillis();
		f.invoke(null, args);
		return System.currentTimeMillis() - start;
	}

	public static void main(String[] args) {
		// Длина массива должна быть достаточно большой
		// для демонстрации времени работы алгоритмов
		Integer[] A = new Integer[100000];
		for (int i = 0; i < A.length; i++)
			A[i] = Integer.valueOf((A.length - 1 - i) + "");

		// Расчет затраченного времени
		long _elapsed = 0, elapsed = 0;
		try {
			_elapsed = elapsedTime(
					Task1.class.getMethod("_mergeSort", Integer[].class, Integer.class),
					A, A.length);
			elapsed = elapsedTime(
					Task1.class.getMethod("mergeSort", Integer[].class, Integer.class),
					A, A.length);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		// Запуск процессов
		try {
			Ws.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Ws.shutdown();
		System.out.println(_elapsed);
		System.out.println(elapsed);
	}
}
