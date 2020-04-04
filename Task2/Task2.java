import java.util.*;
import java.util.concurrent.*;

public class Task2 {
    private static final double eps = 0.01; // Точность, чем меньше, тем ближе к значению интеграла
    private static final int p = 4; // Ограничение на число процессов
    private static int n = 0, i = 0; // Счетчики процессов

    public static ExecutorService service = Executors.newFixedThreadPool(p); // Параллельное выполнение

    // Пример функции f(x)
    // Парабола, точки пересечения с OX: +-2
    public static double f(double x) {
        return -x * x + 4;
    }

    /**
     * quad параллельное вычисление площади фигуры, ограниченной ab, f(a), f(b) и f(x)
     * @param l = a
     * @param r = b
     * @param fl = f(a)
     * @param fr = f(b)
     * @param lrarea площадь
     * @return площадь
     */
    public static double quad(double l, double r, double fl, double fr, double lrarea) {
        double m = (l + r) / 2;
        double fm = f(m);
        final double[] larea = {(fl + fm) * (m - l) / 2};
        final double[] rarea = {(fm + fr) * (r - m) / 2};

        List<Callable<Double>> tasks = new ArrayList<>(); // Список процессов

        if (Math.abs(lrarea - (larea[0] + rarea[0])) > eps) {
            // Если не превышено ограничение
            if (n < p) { // n(t) < p - 1
                // Инициализация процесса
                Callable<Double> area = () -> {
                    // Вычисление площади
                    larea[0] = quad(l, m, fl, fm, larea[0]);
                    rarea[0] = quad(m, r, fm, fr, rarea[0]);
                    System.out.println("Процесс: " + ++i + ", Площадь: " + (larea[0] + rarea[0]));
                    return larea[0] + rarea[0];
                };
                tasks.add(area);
                n++;
            // Иначе последовательное выполнение
            } else {
                larea[0] = _quad(l, m, fl, fm, larea[0]);
                rarea[0] = _quad(m, r, fm, fr, rarea[0]);
            }
        }
        // Запуск процессов
        try {
            service.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return larea[0] + rarea[0];
    }

    /**
     * quad последовательное вычисление площади фигуры, ограниченной ab, f(a), f(b) и f(x)
     * @param l = a
     * @param r = b
     * @param fl = f(a)
     * @param fr = f(b)
     * @param lrarea площадь
     * @return площадь
     */
    public static double _quad(double l, double r, double fl, double fr, double lrarea) {
        double m = (l + r) / 2;
        double fm = f(m);
        double larea = (fl + fm) * (m - l) / 2;
        double rarea = (fm + fr) * (r - m) / 2;

        if (Math.abs(lrarea - (larea + rarea)) > eps) {
            larea = _quad(l, m, fl, fm, larea);
            rarea = _quad(m, r, fm, fr, rarea);
        }
        System.out.println("Площадь: " + (larea + rarea));
        return larea + rarea;
    }

    public static void main(String[] args) {
        double a = -2, b = 2, fa = f(a), fb = f(b), area = (fa + fb) * (b - a) / 2;
        System.out.println("Результат: " + _quad(a, b, fa, fb, area)); // Последовательно
        System.out.println("Результат: " + quad(a, b, fa, fb, area)); // Параллельно
    }
}
