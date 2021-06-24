package tall.test;

public class MatrixTest {

    public static void main(String[] args) {
        int[][] a = new int[3][];

        a[0] = new int[]{1, 2,};
        a[1] = new int[]{1, 2,};
        a[2] = new int[]{1, 2,};
        // 打印原始矩阵 a
       printMatrix(a);

        // 转置
        // 转置后的矩阵，使用at表示
        int[][] at = new int[2][3];

//        for (int j = 0; j < a[0].length; j++) {
//            at[j] = new int[a.length];
//            for (int i = 0; i < a.length; i++) {
//                at[j][i] = a[i][j];
//            }
//        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                at[j][i] = a[i][j];
            }
        }

        // 打印转置后的矩阵 at
        printMatrix(at);
    }

    // 矩阵打印
    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "");
            }
            System.out.println();
        }
    }

}
