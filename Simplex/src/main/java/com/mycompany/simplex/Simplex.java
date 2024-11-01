package com.mycompany.simplex;
import java.util.Scanner;

public class Simplex {

    private static void printTable(double[][] table) {
        for (double[] row : table) {
            for (double value : row) {
                System.out.printf("%10.2f ", value);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод количества переменных и ограничений
        System.out.print("Введите количество переменных: ");
        int numVariables = scanner.nextInt();
        System.out.print("Введите количество ограничений: ");
        int numConstraints = scanner.nextInt();

        // Ввод коэффициентов целевой функции
        double[] objectiveFunction = new double[numVariables]; // Без свободного члена
        System.out.println("Введите коэффициенты целевой функции (Z):");
        for (int i = 0; i < numVariables; i++) {
            System.out.printf("Коэффициент x%d: ", (i + 1));
            objectiveFunction[i] = scanner.nextDouble();
        }
        
        // Ввод ограничений
        double[][] constraints = new double[numConstraints][numVariables + 1]; // +1 для свободного члена
        for (int i = 0; i < numConstraints; i++) {
            System.out.println("Введите ограничения для ограничения " + (i + 1) + ":");
            for (int j = 0; j < numVariables; j++) {
                System.out.printf("Коэффициент x%d: ", (j + 1));
                constraints[i][j] = scanner.nextDouble();
            }
            System.out.print("Свободный член: ");
            constraints[i][numVariables] = scanner.nextDouble(); // Свободный член
        }

        // Построение симплекс-таблицы
        double[][] simplexTable = new double[numConstraints + 1][numVariables + numConstraints + 1];

        // Заполнение симплекс-таблицы
        for (int i = 0; i < numConstraints; i++) {
            for (int j = 0; j < numVariables; j++) {
                simplexTable[i][j] = constraints[i][j];
            }
            simplexTable[i][numVariables + i] = 1; // Добавление искусственной переменной
            simplexTable[i][numVariables + numConstraints] = constraints[i][numVariables]; // Свободный член
        }

        for (int j = 0; j < numVariables; j++) {
            simplexTable[numConstraints][j] = -objectiveFunction[j]; // Целевая функция с отрицательными коэффициентами
        }

        // Основной цикл симплекс-метода
        while (true) {
            // Поиск входящей переменной
            int enteringVarIndex = -1;
            double minValue = Double.MAX_VALUE;
            for (int j = 0; j < numVariables + numConstraints; j++) {
                if (simplexTable[numConstraints][j] < minValue) {
                    minValue = simplexTable[numConstraints][j];
                    enteringVarIndex = j;
                }
            }

            if (minValue >= 0) break; // Оптимальное решение найдено

            // Поиск выходящей переменной
            int exitingVarIndex = -1;
            double minRatio = Double.MAX_VALUE;
            for (int i = 0; i < numConstraints; i++) {
                if (simplexTable[i][enteringVarIndex] > 0) {
                    double ratio = simplexTable[i][numVariables + numConstraints] / simplexTable[i][enteringVarIndex];
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        exitingVarIndex = i;
                    }
                }
            }

            if (exitingVarIndex == -1) break; // Проблема неограниченности

            // Обновление таблицы
            double pivotValue = simplexTable[exitingVarIndex][enteringVarIndex];
            
            // Делим строку выходящей переменной на опорный элемент
            for (int j = 0; j <= numVariables + numConstraints; j++) {
                simplexTable[exitingVarIndex][j] /= pivotValue;
            }

            // Обновляем остальные строки
            for (int i = 0; i <= numConstraints; i++) {
                if (i != exitingVarIndex) {
                    double factor = simplexTable[i][enteringVarIndex];
                    for (int j = 0; j <= numVariables + numConstraints; j++) {
                        simplexTable[i][j] -= factor * simplexTable[exitingVarIndex][j];
                    }
                }
            }

            printTable(simplexTable); // Вывод текущей таблицы
        }

        // Вывод оптимального значения целевой функции Z
        System.out.println("Оптимальное значение целевой функции Z: " + (-simplexTable[numConstraints][numVariables + numConstraints]));

        // Вывод значений переменных x1 и x2
        for (int i = 0; i < numConstraints; i++) {
            if (simplexTable[i][numVariables + numConstraints] > 0) { // Проверяем, является ли это базисной переменной
                int basicVariableIndex = -1;
                for (int j = 0; j < numVariables + numConstraints; j++) { 
                    if (simplexTable[i][j] == 1) { 
                        basicVariableIndex = j;
                        break;
                    }
                }
                if(basicVariableIndex != -1 && basicVariableIndex < 2){ 
                    System.out.printf("Значение x%d: %.2f%n", basicVariableIndex + 1, simplexTable[i][numVariables + numConstraints]);
                }
            }
        }
    }
}