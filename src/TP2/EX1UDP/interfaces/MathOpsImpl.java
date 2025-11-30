package TP2.EX1UDP.interfaces;

import TP2.EX1TCP.interfaces.MathOps;

public class MathOpsImpl implements MathOps {

    @Override
    public long factorielle(int n) {
        long r = 1;
        for (int i = 1; i <= n; i++) r *= i;
        return r;
    }

    @Override
    public long puissance(int base, int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) result *= base;
        return result;
    }

    @Override
    public double racineCarree(int x) {
        return Math.sqrt(x);
    }

    @Override
    public String equationSecondDegre(double a, double b, double c) {
        double delta = b*b - 4*a*c;

        if (delta < 0) {
            return "Pas de solutions réelles.";
        } else if (delta == 0) {
            double x = -b / (2*a);
            return "Solution unique : x = " + x;
        } else {
            double x1 = (-b - Math.sqrt(delta)) / (2*a);
            double x2 = (-b + Math.sqrt(delta)) / (2*a);
            return "Deux solutions : x1 = " + x1 + ", x2 = " + x2;
        }
    }
}
