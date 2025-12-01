package TP2.EX2UDP;

import TP2.EX2TCP.services.Service;

public class ServiceImpl implements Service {

    @Override
    public double traiterBonus(double salaire) {
        return salaire + salaire * 0.25;
    }

    @Override
    public double traiterInsurance(double salaire) {
        return salaire - salaire * 0.05;
    }

    @Override
    public double traiterTax(double salaire) {
        return salaire - salaire * 0.15;
    }

    @Override
    public double traiterSalaireNet(double salaire) {
        salaire += salaire * 0.25;  // Bonus
        salaire -= salaire * 0.05;  // Insurance
        salaire -= salaire * 0.15;  // Tax
        return salaire;
    }
}
