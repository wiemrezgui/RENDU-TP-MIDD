package TP2.EX2UDP;

public interface Service {
    double traiterBonus(double salaire);
    double traiterInsurance(double salaire);
    double traiterTax(double salaire);
    double traiterSalaireNet(double salaire);
}
